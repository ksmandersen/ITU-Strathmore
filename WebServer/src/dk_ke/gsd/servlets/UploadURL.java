//file Serve.java
package dk_ke.gsd.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class UploadURL extends HttpServlet {
// private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException { 
	 	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	 	String camera = req.getParameter("camera");
	 	String timestamp = req.getParameter("date");
	 	//TODO Crash if there is no roomId or timestamp
		String blobUploadUrl = blobstoreService.createUploadUrl("/images/blob_upload?camera="+camera+"&date="+timestamp);
		JSONObject json = new JSONObject();
		try {
			json.put("url", blobUploadUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		res.setContentType("application/json");
		res.getWriter().write(json.toString());
     } 
}
