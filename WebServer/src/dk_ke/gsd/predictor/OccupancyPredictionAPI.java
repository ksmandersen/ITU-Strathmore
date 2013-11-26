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

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

@Api(name = "occupancyPredictionAPI", version = "v1", scopes = { "https://www.googleapis.com/auth/userinfo.email" },
// This will have more clientIds when we create them. So far this is only the
// web client id.
clientIds = { "390025593484.apps.googleusercontent.com" })
public class OccupancyPredictionAPI {
	private static final Logger log = Logger
			.getLogger(OccupancyPredictionAPI.class.getName());
	private static final int RESULT_HARD_LIMIT = 10000;

	/**
	 * This method lists all the entities inserted in datastore having the
	 * specified day of week. It uses HTTP GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws BadRequestException 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listObservationsByDayOfWeek", path = "observations_by_day_of_week")
	public CollectionResponse<Observation> listObservationByDayOfWeek(

	@Nullable @Named("dayOfWeek") Integer day,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) throws BadRequestException {
		
		EntityManager mgr = null;
		Cursor cursor = null;
		List<Observation> execute = null;
		Query query = null;

		try {
			mgr = EMF.getEntityManager();
			log.log(Level.INFO, "Starting query");
			long startTime = System.currentTimeMillis();
			if (day != null && (day >= 1 && day <= 7)) {
				
				query = mgr
						.createQuery("select from Observation as Observation");
			}else {
				throw new BadRequestException(day + " is not a valid weekday, days range from 1-7 (Sunday = 1, Saturday = 7");
			}
					
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null && limit < RESULT_HARD_LIMIT) {
				query.setMaxResults(limit);
			} else {
				log.log(Level.WARNING,
						"Limit larger than accepted hard limit. Server will use hard limit: "
								+ RESULT_HARD_LIMIT);
				query.setMaxResults(RESULT_HARD_LIMIT);
				query.setFirstResult(0);
			}

			List<Observation> results = query.getResultList();
			long endTime = System.currentTimeMillis();
			log.log(Level.INFO, "Query finished after " +(endTime - startTime) + " milliseconds");
			execute = Queries.getObservationsByDayOfWeek(day, results);
			
			log.log(Level.INFO, "Resultset size: " + execute.size());
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();
			long startTime2 = System.currentTimeMillis();
			log.log(Level.INFO, (startTime2 - endTime) + " milliseconds passed");
		
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (Observation obj : execute)
				;

		} finally {
			mgr.close();
		}
		return CollectionResponse.<Observation> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listObservations", path = "observations")
	public CollectionResponse<Observation> listObservation(

	@Nullable @Named("dateTimeFrom") Date from,
			@Nullable @Named("dateTimeTo") Date to,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Observation> execute = null;
		Query query = null;
		try {
			mgr = EMF.getEntityManager();
			if (from != null && to == null) {
				query = Queries.getObservationsFromDate(from, mgr);
			} else if (from == null && to != null) {
				query = Queries.getObservationsBeforeDate(to, mgr);
			} else if (from != null && to != null) {
				query = Queries.getObservationsBetweenDates(from, to, mgr);
			} else {
				query = mgr
						.createQuery("select from Observation as Observation");
			}

			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null && limit < RESULT_HARD_LIMIT) {
				query.setMaxResults(limit);
			} else {
				log.log(Level.WARNING,
						"Limit larger than accepted hard limit. Server will use hard limit: "
								+ RESULT_HARD_LIMIT);
				query.setMaxResults(RESULT_HARD_LIMIT);
			}

			query.setFirstResult(0);
			execute = (List<Observation>) query.getResultList();
			log.log(Level.INFO, "Resultset size: " + execute.size());
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (Observation obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Observation> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getObservation")
	public Observation getObservation(@Named("id") Long id) {
		EntityManager mgr = EMF.getEntityManager();
		Observation observation = null;
		try {
			observation = mgr.find(Observation.class, id);
		} finally {
			mgr.close();
		}
		return observation;
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

	@ApiMethod(name = "uploadUrl", path = "upload/url", httpMethod = HttpMethod.GET)
	public List<String> getUploadUrl() {
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		String blobUploadUrl = blobstoreService.createUploadUrl("/blob/upload");
		ArrayList<String> al = new ArrayList<String>();
		al.add(blobUploadUrl);
		return al;
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
