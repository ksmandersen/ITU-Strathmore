package dk_ke.gsd.predictor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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

	public static List<Observation> filterObservationsByDayOfWeek(int day,
			List<Observation> resultList) {
		Calendar cal = Calendar.getInstance();
		List<Observation> filteredResults = new ArrayList<Observation>();
		for (Observation observation : resultList) {
			cal.setTime(observation.getCaptureDate());
			if (cal.get(Calendar.DAY_OF_WEEK) == day) {
				filteredResults.add(observation);
			}
		}
		return filteredResults;
	}

	public static boolean probabilityShouldBeRecalculated(Camera camera,
			EntityManager em) {

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();

		Query query = em
				.createQuery("SELECT p FROM Probability p WHERE p.camera = :camera ORDER BY p.snapshotDate desc");
		query.setParameter("camera", camera);
		query.setMaxResults(1);

		Probability prob = null;
		try {
			prob = (Probability) query.getSingleResult();
			long diff = now.getTime() - prob.getSnapshotDate().getTime();
			if (diff < DAYS_BETWEEN_PROBABILITY_RECALCULATIONS_IN_MILLIS) {
				return false;
			} else {
				return true;
			}
		} catch (NoResultException e) {
			e.printStackTrace();
			return true;
		}

	}

	@SuppressWarnings("unchecked")
	public static List<Probability> createAndReturnUpdatedProbabilities(
			Camera camera, EntityManager em) {
		Query query = em
				.createQuery("SELECT o FROM Observation o WHERE o.camera = :camera");
		query.setParameter("camera", camera);
		List<Observation> observations = query.getResultList();
		log.log(Level.INFO, "initial query contained: " + observations.size()
				+ " elements");
		Calendar c = Calendar.getInstance();
		Probability prob = new Probability();
		prob.setCamera(camera);
		prob.setSnapshotDate(c.getTime());

		for (int i = 1; i < 8; i++) {
			List<Observation> filteredResults = filterObservationsByDayOfWeek(
					i, observations);
			double totalObservationsForDay = filteredResults.size();
			log.log(Level.INFO, "totalObservationsForDay " + i
					+ " evaluated to: " + totalObservationsForDay);
			double totalTrueObservations = 0;
			for (Observation observation : filteredResults) {
				if (observation.isOccupancy()) {
					// log.log(Level.INFO, "isOccupancy() evaluated to: "
					// +observation.isOccupancy());
					totalTrueObservations++;
				}
			}
			log.log(Level.INFO, "totalTrueObservations for day " +i+ " evaluated to: "
					+ totalTrueObservations);
			
			double probabilityAsDecimal = -1;
			
			if (filteredResults.size() > 0) {
				probabilityAsDecimal = totalTrueObservations
						/ totalObservationsForDay;
			}

			log.log(Level.INFO, "probabilityAsDecimal for day " +i+ " evaluated to: "
					+ probabilityAsDecimal);
			switch (i) {
			case 1:// Sunday
				prob.setSunday(probabilityAsDecimal);
				break;
			case 2:// Monday
				prob.setMonday(probabilityAsDecimal);
				break;
			case 3:// Tuesday
				prob.setTuesday(probabilityAsDecimal);
				break;
			case 4:// Wednesday
				prob.setWednesday(probabilityAsDecimal);
				break;
			case 5:// Thursday
				prob.setThursday(probabilityAsDecimal);
				break;
			case 6:// Friday
				prob.setFriday(probabilityAsDecimal);
				break;
			case 7:// Saturday
				prob.setSaturday(probabilityAsDecimal);
				break;

			default:
				break;
			}

		}

		em.getTransaction().begin();
		em.persist(prob);
		em.getTransaction().commit();

		List<Probability> returnList = new ArrayList<>();
		returnList.add(prob);

		return returnList;
	}

	public static List<Probability> returnLatestProbabilities(Camera camera,
			EntityManager em) {
		Query query = em
				.createQuery("SELECT p FROM Probability p WHERE p.camera = :camera ORDER BY p.snapshotDate desc");
		query.setParameter("camera", camera);
		query.setMaxResults(1);
		Probability prob = (Probability) query.getSingleResult();
		List<Probability> returnList = new ArrayList<>();
		returnList.add(prob);

		return returnList;
	}

}
