package dk_ke.gsd.images;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.blobstore.BlobKey;

import dk_ke.gsd.predictor.Camera;

@Entity
public class Image {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectId;
	private BlobKey blobKey;	
	@Enumerated(EnumType.STRING)
	private Camera camera;
	private Long unixCaptureTimestap;
	private Date captureTimestamp;
	
	public Image(Camera camera, Long unixTimestamp, BlobKey blobkey) {
		setCamera(camera);
		setUnixCaptureTimestap(unixTimestamp);		
		setBlobKey(blobkey);
	}
	
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public BlobKey getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public Long getUnixCaptureTimestap() {
		return unixCaptureTimestap;
	}
	public void setUnixCaptureTimestap(Long unixCaptureTimestap) {
		this.unixCaptureTimestap = unixCaptureTimestap;
		setCaptureDate(new Date(unixCaptureTimestap*1000));
	}
	public Date getCaptureDate() {
		return captureTimestamp;
	}
	public void setCaptureDate(Date takenDate) {
		this.captureTimestamp = takenDate;
	}
	
}
