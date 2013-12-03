package dk_ke.gsd.predictor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.google.appengine.api.datastore.AdminDatastoreService.QueryBuilder;

public class Queries {
	private static final Logger log = Logger.getLogger(Queries.class.getName());
	private static final int DAYS_BETWEEN_PROBABILITY_RECALCULATIONS = 7;
	private static final long DAYS_BETWEEN_PROBABILITY_RECALCULATIONS_IN_MILLIS = TimeUnit.DAYS
			.toMillis(DAYS_BETWEEN_PROBABILITY_RECALCULATIONS);

	public static Query getObservationsByDate(Date date, EntityManager em) {
		return em
				.createQuery(
						"SELECT o FROM Observation o WHERE o.captureDate = :captureDate")
				.setParameter("captureDate", date);
	}

	public static Query getObservationsFromDate(Date date, EntityManager em) {
		return em
				.createQuery(
						"SELECT o FROM Observation o WHERE o.captureDate > :captureDate")
				.setParameter("captureDate", date);

	}

	public static Query getObservationsBeforeDate(Date date, EntityManager em) {
		return em
				.createQuery(
						"SELECT o FROM Observation o WHERE o.captureDate < :captureDate")
				.setParameter("captureDate", date);
	}

	public static Query getObservationsBetweenDates(Date from, Date to,
			EntityManager em) {
		return em
				.createQuery(
						"SELECT o FROM Observation o WHERE o.captureDate > :captureDateFrom AND o.captureDate < :captureDateTo")
				.setParameter("captureDateFrom", from)
				.setParameter("captureDateTo", to);
	}

	@SuppressWarnings("unchecked")
	public static List<Probability> returnUpdatedProbabilities(
			Camera camera, DayOfTheWeek dayOfTheWeek, Integer timeOfDay,
			EntityManager em) {

		Calendar c = Calendar.getInstance();
		Probability prob = new Probability();
		prob.setSnapshotDate(c.getTime());

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Observation> cq = cb.createQuery(Observation.class);
		Root<Observation> observation = cq.from(Observation.class);
		cq.select(observation);

		if (camera != null) {
			prob.setCamera(camera);
			cq.where(cb.equal(observation.get("camera"), camera));
		}
		if (dayOfTheWeek != null) {
			prob.setDay(dayOfTheWeek);
			cq.where(cb.equal(observation.get("day"), dayOfTheWeek));
		}
		if (timeOfDay != null) {
			prob.setTimeOfday(timeOfDay);
			cq.where(cb.equal(observation.get("timeOfDay"), timeOfDay));
		}

		Query query = em.createQuery(cq);

		List<Observation> observations = query.getResultList();

		log.log(Level.INFO, "initial query contained: " + observations.size()
				+ " elements");

		double totalObservations = observations.size();
		double totalTrueObservations = 0;
		for (Observation obs : observations) {
			if (obs.isOccupancy()) {
				totalTrueObservations++;
			}
		}
		log.log(Level.INFO, "totalTrueObservations evaluated to: "
				+ totalTrueObservations);

		double probabilityAsDecimal = -1;
		if (observations.size() > 0) {
			probabilityAsDecimal = totalTrueObservations / totalObservations;
		}

		log.log(Level.INFO, "probabilityAsDecimal evaluated to: "
				+ probabilityAsDecimal);

		prob.setProbabilityOfOccupancy(probabilityAsDecimal);

		List<Probability> returnList = new ArrayList<>();
		returnList.add(prob);

		return returnList;
	}

	@SuppressWarnings("unchecked")
	public static List<ProbabilityContainer> returnAllProbabilities(Camera camera,
			EntityManager em) {
		
		List<ProbabilityContainer> returnList = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		// For each day of the week
		for (int i = 1; i <= 7; i++) {
			// Create new ProbabilityContainer object and initialize known fields
			ProbabilityContainer container = new ProbabilityContainer();
			container.setSnapshotDate(c.getTime());
			container.setCamera(camera);
			container.setDay(DayOfTheWeek.fromCalendarDay(i));
			List<Probability> probabilities = new ArrayList<>();
			container.setProbabilities(probabilities);
			// Initialize CriteriaBuilder object for the current day
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Observation> cq = cb.createQuery(Observation.class);
			Root<Observation> observation = cq.from(Observation.class);
			cq.select(observation);
			cq.where(cb.equal(observation.get("camera"), camera));
			cq.where(cb.equal(observation.get("day"), DayOfTheWeek.fromCalendarDay(i)));
			// For each hour of the day
			for (int j = 0; j <= 23; j++) {
				cq.where(cb.equal(observation.get("timeOfDay"), j));
				Query query = em.createQuery(cq);
				List<Observation> observations = query.getResultList();
				
				// Calculate the probability of a true observation for a given hour of day
				double totalObservations = observations.size();
				double totalTrueObservations = 0;
				for (Observation obs : observations) {
					if (obs.isOccupancy()) {
						totalTrueObservations++;
					}
				}
				
				double probabilityAsDecimal = -1;
				if (totalObservations > 0) {
					probabilityAsDecimal = totalTrueObservations / totalObservations;
				}
				
				Probability prob = new Probability();
				prob.setTimeOfday(j);
				prob.setProbabilityOfOccupancy(probabilityAsDecimal);
				probabilities.add(prob);
				
			}
			
			returnList.add(container);
		}
		
		return returnList;
	}
	
	private static boolean stringNotNullOrEmpty(Object obj) {
		return (obj != null && ((String) obj).trim() != "");
	}


}
