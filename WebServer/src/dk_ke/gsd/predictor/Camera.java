//package dk_ke.gsd.predictor;
//
//import java.util.Collection;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
////import javax.persistence.GeneratedValue;
////import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//
//@Entity
//public class Camera {
//	
//    //@GeneratedValue(strategy = GenerationType.IDENTITY)
//    //private Long objectId;
//	
//    @Id
//    private String name;
//	private String location;
//	@OneToMany(mappedBy="camera", cascade=CascadeType.ALL)
//	private Collection<Observation> observations;
//	
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getLocation() {
//		return location;
//	}
//	public void setLocation(String location) {
//		this.location = location;
//	}	
//}
