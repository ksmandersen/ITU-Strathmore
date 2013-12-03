package dk_ke.gsd.predictor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Probability {

	private Date snapshotDate;
	private Camera camera;
	private DayOfTheWeek day;
	private Integer timeOfday;
	private Double probabilityOfOccupancy;
	
	public Date getSnapshotDate() {
		return snapshotDate;
	}
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public DayOfTheWeek getDay() {
		return day;
	}
	public void setDay(DayOfTheWeek day) {
		this.day = day;
	}
	public Integer getTimeOfday() {
		return timeOfday;
	}
	public void setTimeOfday(Integer timeOfday) {
		this.timeOfday = timeOfday;
	}
	public Double getProbabilityOfOccupancy() {
		return probabilityOfOccupancy;
	}
	public void setProbabilityOfOccupancy(Double probabilityOfOccupancy) {
		this.probabilityOfOccupancy = probabilityOfOccupancy;
	}
	
}
