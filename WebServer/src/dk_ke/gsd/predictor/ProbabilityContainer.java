package dk_ke.gsd.predictor;

import java.util.Date;
import java.util.List;

public class ProbabilityContainer {

	private DayOfTheWeek day;
	private Date snapshotDate;
	private Camera camera;
	
	private List<Probability> probabilities;

	public List<Probability> getProbabilities() {
		return probabilities;
	}

	public DayOfTheWeek getDay() {
		return day;
	}

	public void setDay(DayOfTheWeek day) {
		this.day = day;
	}

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

	public void setProbabilities(List<Probability> probabilities) {
		this.probabilities = probabilities;
	}
}
