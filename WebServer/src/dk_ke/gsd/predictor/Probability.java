package dk_ke.gsd.predictor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Probability {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectId;
	
	private Date snapshotDate;
	
	@Enumerated(EnumType.STRING)
	private Camera camera;
	
	private double monday;
	private double tuesday;
	private double wednesday;
	private double thursday;
	private double friday;
	private double saturday;
	private double sunday;
	
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
	public double getMonday() {
		return monday;
	}
	public void setMonday(double monday) {
		this.monday = monday;
	}
	public double getTuesday() {
		return tuesday;
	}
	public void setTuesday(double tuesday) {
		this.tuesday = tuesday;
	}
	public double getWednesday() {
		return wednesday;
	}
	public void setWednesday(double wednesday) {
		this.wednesday = wednesday;
	}
	public double getThursday() {
		return thursday;
	}
	public void setThursday(double thursday) {
		this.thursday = thursday;
	}
	public double getFriday() {
		return friday;
	}
	public void setFriday(double friday) {
		this.friday = friday;
	}
	public double getSaturday() {
		return saturday;
	}
	public void setSaturday(double saturday) {
		this.saturday = saturday;
	}
	public double getSunday() {
		return sunday;
	}
	public void setSunday(double sunday) {
		this.sunday = sunday;
	}
}
