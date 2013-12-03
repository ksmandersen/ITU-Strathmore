//file Upload.java
package dk_ke.gsd.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import dk_ke.gsd.images.Image;
import dk_ke.gsd.predictor.Camera;
import dk_ke.gsd.predictor.EMF;

public class Upload extends HttpServlet {
 private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

 @SuppressWarnings({ "unused", "deprecation" })
@Override
 public void doPost(HttpServletRequest req, HttpServletResponse res)
     throws ServletException, IOException {

	 Camera camera = Camera.valueOf(req.getParameter("room-id"));
	 Long timestamp = new Long(req.getParameter("date"));
	 
	 //TODO crash if no params set
	 
	 Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	 BlobKey blobKey = new ArrayList<BlobKey>(blobs.values()).get(0);
	 
	 System.out.println(blobKey.getKeyString());
	 System.out.println("ROOM ID: " + camera + " TIMESTAMP: " + timestamp + " BLOBKEY: " + blobKey);
	 
//	 Camera camera = Camera.valueOf("CAM_01");
//	 Long timestamp = (long) 1385999685;
	 Image img = new Image(camera, timestamp, blobKey);
	 EntityManager mgr = EMF.getEntityManager();
	 try {
		 mgr.persist(img);
	 } finally {
		 mgr.close();
	 }
	 
	 System.out.println(img.getBlobKey());
	 System.out.println(img.getCaptureDate());
 }
}