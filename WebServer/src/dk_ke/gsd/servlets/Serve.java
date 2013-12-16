//file Serve.java
package dk_ke.gsd.servlets;

import java.io.IOException;


import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.Query;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import dk_ke.gsd.predictor.EMF;
import dk_ke.gsd.images.Image;

public class Serve extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

 	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
	   EntityManager mgr = null;
	   Image img = null;
	   try {
		 String camera = req.getParameter("camera");
		 mgr = EMF.getEntityManager();
		 Query query = mgr.createQuery("SELECT i FROM Image i where i.camera = :camera ORDER BY captureTimestamp DESC", Image.class);
		 query.setParameter("camera", camera);
		 query.setMaxResults(1);
		 
		 img = (Image)query.getSingleResult();
		 } finally {
			 mgr.close();
		 }
		  
		 BlobKey blobKey = img.getBlobKey();
	     blobstoreService.serve(blobKey, res);
     }    
}
