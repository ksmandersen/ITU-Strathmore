package dk_ke.gsd.predictor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import dk_ke.gsd.images.Image;
import dk_ke.gsd.images.UploadUrl;

@Api(name = "occupancyPredictionAPI", version = "v1", scopes = { "https://www.googleapis.com/auth/userinfo.email" },
// This will have more clientIds when we create them. So far this is only the
// web client id.
clientIds = { "390025593484.apps.googleusercontent.com" })
public class OccupancyPredictionAPI {
	private static final Logger log = Logger
			.getLogger(OccupancyPredictionAPI.class.getName());
	
	@ApiMethod(name = "listAllProbabilities", path = "all_probabilities")
	public CollectionResponse<ProbabilityContainer> listAllProbabilities(
			@Named("camera") Camera camera) throws BadRequestException {
		List<ProbabilityContainer> result = null;
		if (camera != null) {
			result = Queries.returnAllProbabilities(camera);
		} else {
			throw new BadRequestException("Please specify camera");
		}
		return CollectionResponse.<ProbabilityContainer> builder().setItems(result).build();
	}

	@SuppressWarnings({ "unused" })
	@ApiMethod(name = "getProbability", path = "probability")
	public CollectionResponse<Probability> getProbability(
			@Nullable @Named("camera") Camera camera,
			@Nullable @Named("dayOfTheWeek") DayOfTheWeek dayOfTheWeek,
			@Nullable @Named("timeOfDay") Integer timeOfDay)
					throws BadRequestException {
		EntityManager mgr = null;
		List<Probability> result = null;
		Query query = null;

		if (timeOfDay != null) {
			if (timeOfDay < 0 || timeOfDay > 23) {
				throw new BadRequestException("timeOfDay should be from 0-23");
			}
		}

		try {
			mgr = EMF.getEntityManager();
			result = Queries.returnUpdatedProbabilities(camera, dayOfTheWeek,
					timeOfDay, mgr);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<Probability> builder().setItems(result)
				.build();
	}

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listObservations", path = "observations")
	public CollectionResponse<Observation> listObservation(
			@Nullable @Named("from") Date from,
			@Nullable @Named("to") Date to,
			@Nullable @Named("camera") String camera) {

		EntityManager mgr = null;
		List<Observation> execute = null;
		Query query = null;
		try {
			mgr = EMF.getEntityManager();
			CriteriaBuilder cb = mgr.getCriteriaBuilder();
	        CriteriaQuery<Observation> cq = cb.createQuery(Observation.class);
	        Root<Observation> observation = cq.from(Observation.class);
	        cq.select(observation);	        

	        ArrayList<Predicate> predicates = new ArrayList<Predicate>();
	        if (present(from)) {
	        	predicates.add(cb.greaterThanOrEqualTo(observation.<Date>get("captureDate"), from));	
	        }
	        if (present(to)) {
	        	predicates.add(cb.lessThanOrEqualTo(observation.<Date>get("captureDate"), to));
	        }
	        if (present(camera)) {
	        	predicates.add(cb.equal(observation.get("camera"), camera));
	        }
	        cq.where(predicates.toArray(new Predicate[predicates.size()]));

	        query = mgr.createQuery(cq);
	        execute = (List<Observation>) query.getResultList(); 
			
			
			log.log(Level.INFO, "Resultset size: " + execute.size());
			// Tight loop for fetching all entities from datastore and
			// accomodate for lazy fetch.
			for (Observation obj : execute)
				;
		} finally {
			mgr.close();
		}
		return CollectionResponse.<Observation> builder().setItems(execute).build();
	}


	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param observation
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertObservation")
	public Observation insertObservation(Observation observation) {
		EntityManager mgr = EMF.getEntityManager();
		try {
			if (containsObservation(observation)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(observation);
		} finally {
			mgr.close();
		}
		return observation;
	}

	@ApiMethod(name = "addObservations", path = "add_observations", httpMethod = HttpMethod.POST)
	public List<Observation> addObservations(
			ObservationListContainer observationList) {
		List<Observation> observations = observationList.getObservationList();
		EntityManager mgr = null;

		for (Observation obs : observations) {
			try {
				mgr = EMF.getEntityManager();
				if (containsObservation(obs)) {
					throw new EntityExistsException("Object already exists");
				}
				mgr.persist(obs);
			} finally {
				mgr.close();
			}
		}
		return observations;
	}

	@ApiMethod(name = "uploadUrl", path = "images/upload_url", httpMethod = HttpMethod.GET)
	public UploadUrl uploadUrl(@Named("camera") String camera, @Named("date") String timestamp) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String blobUploadUrl = blobstoreService.createUploadUrl("/images/blob_upload?camera="+camera+"&date="+timestamp);
		UploadUrl url = new UploadUrl();
		url.setUrl(blobUploadUrl);
		return url;
	}

	@ApiMethod(name = "latestImage", httpMethod = HttpMethod.GET)
	public Image latestImage(@Named("camera") String camera) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		EntityManager mgr = null;
		Image img = null;
		try {
			mgr = EMF.getEntityManager();
			Query query = mgr.createQuery("SELECT i FROM Image i where i.camera = :camera ORDER BY captureTimestamp DESC", Image.class);
			query.setParameter("camera", camera);
			query.setMaxResults(1);
			img = (Image)query.getSingleResult();
		} finally {
			mgr.close();
		}
			  
		BlobKey blobKey = img.getBlobKey();
		img.setImage(blobstoreService.fetchData(blobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE-1));
		return img; 
	}
	
	
	private boolean present(Object obj) {	
		if (obj instanceof String) {
			return (obj != null && ((String)obj).trim() != "");
		}
		else {
			return (obj != null);
		}
	}
	
	private boolean containsObservation(Observation observation) {
		EntityManager mgr = EMF.getEntityManager();
		boolean contains = true;
		try {
			Observation item = null;
			if (observation.getObjectId() != null) {
				item = mgr.find(Observation.class, observation.getObjectId());
				if (item == null) {
					contains = false;
				}
			} else {
				contains = false;
			}

		} finally {
			mgr.close();
		}
		return contains;
	}

}
