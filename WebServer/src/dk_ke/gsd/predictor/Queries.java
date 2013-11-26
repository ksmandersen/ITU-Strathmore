package dk_ke.gsd.predictor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class Queries {
	private static final Logger log = Logger
			.getLogger(Queries.class.getName());
	
	public static Query getObservationsByDate(Date date, EntityManager em) {
		return em.createQuery(
			    "SELECT o FROM Observation o WHERE o.captureDate = :captureDate")
			    .setParameter("captureDate", date);
	}
	
	public static Query getObservationsFromDate(Date date, EntityManager em) {
		return em.createQuery(
			    "SELECT o FROM Observation o WHERE o.captureDate > :captureDate")
			    .setParameter("captureDate", date);
		
	}
	
	public static Query getObservationsBeforeDate(Date date, EntityManager em) {
		return em.createQuery(
			    "SELECT o FROM Observation o WHERE o.captureDate < :captureDate")
			    .setParameter("captureDate", date);
	}
	
	public static Query getObservationsBetweenDates(Date from, Date to, EntityManager em) {
		return em.createQuery(
			    "SELECT o FROM Observation o WHERE o.captureDate > :captureDateFrom AND o.captureDate < :captureDateTo")
			    .setParameter("captureDateFrom", from)
			    .setParameter("captureDateTo", to);
	}
	
	public static List<Observation> getObservationsByDayOfWeek(int day, List<Observation> resultList) {		
		log.log(Level.INFO, "Starting filter on " + resultList.size() + " items");
		long startTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();		
		List<Observation> filteredResults = new ArrayList<Observation>();
		for (Observation observation : resultList) {
			cal.setTime(observation.getCaptureDate());
			if (cal.get(Calendar.DAY_OF_WEEK) == day) {
				filteredResults.add(observation);
			}
		}
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, "Returning " +filteredResults.size() + " filtered items after " +(endTime - startTime) + " milliseconds");
		return filteredResults;
	}
}
