package dk_ke.gsd.predictor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@Entity
public class Observation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectId;
	private Date captureDate;
	private boolean occupancy;
//	@ManyToOne(cascade=CascadeType.ALL)
//	private Camera camera;
	private String camera;
	
	
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
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
	public String getCamera() {
		return camera;
	}
	public void setCamera(String camera) {
		this.camera = camera;
	}
	

}
