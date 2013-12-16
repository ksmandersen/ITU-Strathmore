package dk_ke.gsd.predictor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
	private static final EntityManagerFactory emfInstance = Persistence
			.createEntityManagerFactory("transactions-optional");

	private EMF() {
	}
	
	public static EntityManager getEntityManager() {
		return emfInstance.createEntityManager();
	}
}