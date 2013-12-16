// MotionDetection.cpp : Defines the entry point for the console application.
//
#define _CRT_SECURE_NO_DEPRECATE

// Contourold.cpp : Defines the entry point for the console application.
//
//#include "stdafx.h"

#include "iostream"
#include "stdlib.h"
#include <vector>

// OpenCV includes.
#include<opencv\cv.h>
#include<opencv\highgui.h>

using namespace std;
using namespace cv;

int main(int argc, char* argv[])
{
	//Create a new window.
	cvNamedWindow("My Window", CV_WINDOW_AUTOSIZE);
	cvNamedWindow("My Window2", CV_WINDOW_AUTOSIZE);

	CvCapture *input;
	//input = cvCaptureFromCAM(1);
	input = cvCaptureFromFile("C:\\Users\\RasmusZimmer\\Desktop\\Video.mov");

	if(!input){
		std::cout << "cant read";
		return 0;
	}
	//cvSetCaptureProperty(input, CV_CAP_PROP_FRAME_WIDTH, 320); 
	//cvSetCaptureProperty(input, CV_CAP_PROP_FRAME_HEIGHT, 240); 

	//Size of the image.
	IplImage* frame = cvQueryFrame(input);
	CvSize imgSize = cvGetSize(frame);

	//Images to use in the program.
	IplImage* greyImage = cvCreateImage( imgSize, IPL_DEPTH_8U, 1);
	IplImage* colourImage;
	IplImage* movingAverage = cvCreateImage( imgSize, IPL_DEPTH_32F, 3);
	IplImage* difference;
	IplImage* temp;
	IplImage* motionHistory = cvCreateImage( imgSize, IPL_DEPTH_8U, 3);

	//Rectangle to use to put around the people.
	CvRect bndRect = cvRect(0,0,0,0);

	//Points for the edges of the rectangle.
	CvPoint pt1, pt2;
	vector<CvPoint> points;
	//Create a font object.
	CvFont font;

	//Capture the movie frame by frame.
	int prevX = 0;
	int numPeople = 0;

	//Buffer to save the number of people when converting the integer
	//to a string.
	char wow[65];

	//The midpoint X position of the rectangle surrounding the moving objects.
	int avgX = 0;

	//Indicates whether this is the first time in the loop of frames.
	bool first = true;

	//Indicates the contour which was closest to the left boundary before the object
	//entered the region between the buildings.
	int closestToLeft = 0;
	//Same as above, but for the right.
	int closestToRight = 320;

	//Write the number of people counted at the top of the output frame.
	cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.8, 0.8, 0, 2);
	int frameCounter = 0;
	//Keep processing frames...
	for(;;)
	{
		frameCounter++;
		std::cout << "frames: " << frameCounter;
		//Get a frame from the input video.
		colourImage = cvQueryFrame(input);

		//If there are no more frames, jump out of the for.
		if( !colourImage )
		{
			break;
		}

		//If this is the first time, initialize the images.
		if(first)
		{
			difference = cvCloneImage(colourImage);
			temp = cvCloneImage(colourImage);
			cvConvertScale(colourImage, movingAverage, 1.0, 0.0);
			first = false;
		}
		//else, make a running average of the motion.
		else
		{
			cvRunningAvg(colourImage, movingAverage, 0.020, NULL);
		}

		//Convert the scale of the moving average.
		cvConvertScale(movingAverage,temp, 1.0, 0.0);

		//Minus the current frame from the moving average.
		cvAbsDiff(colourImage,temp,difference);

		//Convert the image to grayscale.
		cvCvtColor(difference,greyImage,CV_RGB2GRAY);

		//Convert the image to black and white.
		cvThreshold(greyImage, greyImage, 70, 255, CV_THRESH_BINARY);

		//Dilate and erode to get people blobs
		cvDilate(greyImage, greyImage, 0, 18);
		cvErode(greyImage, greyImage, 0, 10);

		//Find the contours of the moving images in the frame.
		CvMemStorage* storage = cvCreateMemStorage(0);
		CvSeq* contour = 0;
		cvFindContours( greyImage, storage, &contour, sizeof(CvContour), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );

		//Process each moving contour in the current frame...+
		long long ii=0;
		for( ; contour != 0; contour = contour->h_next )
		{
			ii+=1;
			//Get a bounding rectangle around the moving object.
			bndRect = cvBoundingRect(contour, 0);
			
			pt1.x = bndRect.x;
			pt1.y = bndRect.y;
			pt2.x = bndRect.x + bndRect.width;
			pt2.y = bndRect.y + bndRect.height;

			//if(ii == 1)
			//{
			//	points.push_back(pt1);
			//}
			
				
			
			//Draw the bounding rectangle around the moving object.
			cvRectangle(colourImage, pt1, pt2, CV_RGB(255,0,0), 1);
			string s = to_string(ii);
			const char * c = s.c_str();
			CvPoint textPoint = pt1;
			textPoint.y -= 10;
			cvPutText(colourImage, c, textPoint, &font, cvScalar(0, 0, 300));
		}

		/*if(points.size() > 1){

		for(int i = 1; i < points.size(); i++)
		{
			cvLine(colourImage, points[i], points[i-1], CV_RGB(255,0,0), 1);
		}

		}*/
		//Show the frame.
		cvShowImage("My Window", colourImage);
		cvShowImage("My Window2", difference);

		
		//Wait for the user to see it.
		cvWaitKey(1);
	}

	// Destroy the image, movies, and window.
	cvReleaseImage(&temp);
	cvReleaseImage(&difference);
	cvReleaseImage(&greyImage);
	cvReleaseImage(&movingAverage);
	cvDestroyWindow("My Window");
	cvReleaseCapture(&input);

	
	return 0;

}

