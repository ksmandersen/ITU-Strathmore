package dk_ke.gsd.predictor;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Observation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectId;
	private Long unixCaptureTimestamp;
	private Date captureDate;
	
	@Enumerated(EnumType.STRING)
	private DayOfTheWeek day;
	private int timeOfDay;
	private boolean occupancy;
	
	@Enumerated(EnumType.STRING)
	private Camera camera;
	
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
		
	public Long getUnixCaptureTimestamp() {
		return unixCaptureTimestamp;
	}
	public void setUnixCaptureTimestamp(Long unixCaptureTimestamp) {
		this.unixCaptureTimestamp = unixCaptureTimestamp;
		setCaptureDate(new Date(unixCaptureTimestamp*1000));
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCaptureDate());
		setDay(DayOfTheWeek.fromCalendarDay(cal.get(Calendar.DAY_OF_WEEK)));
		setTimeOfDay(cal.get(Calendar.HOUR_OF_DAY));
	}
	public Date getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(Date capture) {
		this.captureDate = capture;
	}
	public boolean isOccupancy() {
		return occupancy;
	}
	public void setOccupancy(boolean occupancy) {
		this.occupancy = occupancy;
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
	public int getTimeOfDay() {
		return timeOfDay;
	}
	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}
}
