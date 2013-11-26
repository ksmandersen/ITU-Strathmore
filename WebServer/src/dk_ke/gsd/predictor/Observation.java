package dk_ke.gsd.predictor;

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
	private Long captureDate;
	private boolean occupancy;
	
	@Enumerated(EnumType.STRING)
	private Camera camera;
	
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
		
	public Long getCaptureDate() {
		return captureDate;
	}
	public void setCaptureDate(Long capture) {
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
}
