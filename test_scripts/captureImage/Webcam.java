import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class Webcam {

    public static void main (String args[]){

    System.out.println("Hello, OpenCV");
    // Load the native library.
    System.loadLibrary("opencv_java247");

    VideoCapture camera = new VideoCapture(0);
    if(!camera.isOpened()){
        System.out.println("Camera Error");
    }
    else{
        System.out.println("Camera OK?");
    }
    for(int i=0; i<5; i++) {
    	Mat frame = new Mat();
    	//camera.grab();
        //System.out.println("Frame Grabbed");
        //camera.retrieve(frame);
        //System.out.println("Frame Decoded");

        camera.read(frame);
        System.out.println("Frame Obtained");
        System.out.println("Captured Frame Width " + frame.width());

        Highgui.imwrite("camera"+ i +".jpg", frame);
        System.out.println("OK");
    }
    camera.release();    
    }
}
