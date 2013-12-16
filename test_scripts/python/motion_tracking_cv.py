#!/usr/bin/env python

import cv
import inspect
import math
import time
import datetime
import requests
import json
import calendar


class TrackedObject: 
    def __init__(self, latest_position = (0,0), movement_vector = [], state = "out"):
        self.latest_position=latest_position
        self.movement_vector = movement_vector
        self.state = state

class Target:

    def __init__(self):

        #self.capture = cv.CaptureFromCAM(0)
        self.capture = cv.CaptureFromFile("Video.mov")
        #TODO!
        #cv.SetCaptureProperty(self.capture, cv.CV_CAP_PROP_FRAME_WIDTH, 320)
        #cv.SetCaptureProperty(self.capture, cv.CV_CAP_PROP_FRAME_HEIGHT, 240)
        
        # Creates a new window
        cv.NamedWindow("Target", 1)
        self.tracked_objects = []
        self.last_request = time.time()
        self.observed_occupancy = False

    def send_occupancy(self):
        # send http request
        timestamp = datetime.datetime.utcnow().strftime('%s')
        payload = {'occupancy': self.observed_occupancy, 'camera': 'CAM_02', "unixCaptureTimestamp": timestamp}
        headers = {'content-type': 'application/json'}
        url = "https://itu-strathmore-occupancy.appspot.com/_ah/api/occupancyPredictionAPI/v1/observation"
        r = requests.post(url, data=json.dumps(payload), headers=headers)

        self.last_request = time.time()
        self.observed_occupancy = False

    def send_image(self, image):
        #Post an image 
        timestamp = datetime.datetime.utcnow().strftime('%s')
        filename = str(timestamp)+".png"
        cv.SaveImage(filename, image)
        #url = "http://localhost:8888/images/upload_url"
        #url = "https://itu-strath-occupancy.appspot.com/images/upload_url"
        url = "https://itu-strathmore-occupancy.appspot.com/_ah/api/occupancyPredictionAPI/v1/images/upload_url"
        data = { 'camera': 'CAM_02', 'date': timestamp }
        request_upload_url = requests.get(url, params=data)
        upload_url = str(request_upload_url.json()['url'])
        print upload_url
        files = {'file': open(filename, 'rb')}
        r2 = requests.post(upload_url, files=files) 

    def run(self):
        # Capture first frame to get size
        frame = cv.QueryFrame(self.capture)
        frame_size = cv.GetSize(frame)
        new_size = ( frame_size[0] / 2, frame_size[1] / 2)
        color_image = cv.CreateImage(new_size, 8, 3)
        grey_image = cv.CreateImage(new_size, cv.IPL_DEPTH_8U, 1)
        moving_average = cv.CreateImage(new_size, cv.IPL_DEPTH_32F, 3)
        font = cv.InitFont(cv.CV_FONT_HERSHEY_COMPLEX_SMALL, 1, 1, 0, 1, 1)
        first = True
        k = 0        
        while True:
            k+=1

            captured_image = cv.QueryFrame(self.capture)
            color_image = cv.CreateImage(new_size, captured_image.depth, captured_image.nChannels)
            cv.Resize(captured_image, color_image)
            # Smooth to get rid of false positives
            cv.Smooth(color_image, color_image, cv.CV_GAUSSIAN, 3, 0)

            if first:
                difference = cv.CloneImage(color_image)
                temp = cv.CloneImage(color_image)
                cv.ConvertScale(color_image, moving_average, 1.0, 0.0)
                first = False
            else:
                cv.RunningAvg(color_image, moving_average, 0.020, None)

            # Convert the scale of the moving average.
            cv.ConvertScale(moving_average, temp, 1.0, 0.0)

            # Minus the current frame from the moving average.
            cv.AbsDiff(color_image, temp, difference)

            # Convert the image to grayscale.
            cv.CvtColor(difference, grey_image, cv.CV_RGB2GRAY)

            # Convert the image to black and white.
            cv.Threshold(grey_image, grey_image, 70, 255, cv.CV_THRESH_BINARY)

            # Dilate and erode to get people blobs
            cv.Dilate(grey_image, grey_image, None, 18)
            cv.Erode(grey_image, grey_image, None, 10)

            storage = cv.CreateMemStorage(0)
            contour = cv.FindContours(grey_image, storage, cv.CV_RETR_CCOMP, cv.CV_CHAIN_APPROX_TC89_KCOS)
            points = []
            #cv.DrawContours(color_image, contour, cv.CV_RGB(255,0,0), cv.CV_RGB(255,0,255), 2, 1, 8, (0, 0))
            i = 0
            while contour:
                self.observed_occupancy = True

                bound_rect = cv.BoundingRect(list(contour))

                center_x = bound_rect[0] + (bound_rect[2]/2)
                center_y = bound_rect[1] + (bound_rect[3]/2)
                #if center_y < 200:
                #    continue
                i+=1
                closest_distance = 10000
                closest_object = None
                for to in self.tracked_objects: 
                    current_distance = math.hypot(to.latest_position[0] - center_x, to.latest_position[1] - center_y)
                    closest_distance = min(closest_distance, current_distance)                    
                    #print "DISTANCES: ", str(closest_distance), str(current_distance)
                    if current_distance == closest_distance:
                        closest_object = to

                if closest_object is None:
                    #print "OBJECT IS NEW"
                    self.tracked_objects.append(TrackedObject((center_x, center_y), [(center_x, center_y)], "new"))
                else: 
                    #print "CLOSEST OBJECT: ", closest_object.latest_position
                    closest_object.movement_vector.append((center_x, center_y))
                    closest_object.latest_position = (center_x, center_y)
                #print "AMOUNT OF OBJECTS: ", str(len(self.tracked_objects))

                if closest_object is not None:
                    cv.Line(color_image, closest_object.latest_position, (center_x, center_y), cv.CV_RGB(0,255,0))
                   
                    #closest_x = min(closest_x, to.latest_position[0])
                    #closest_y = min(closest_y, to.latest_position[0])

                contour = contour.h_next()

                pt1 = (bound_rect[0], bound_rect[1])
                pt2 = (bound_rect[0] + bound_rect[2], bound_rect[1] + bound_rect[3])
                points.append(pt1)
                points.append(pt2)
                cv.Rectangle(color_image, pt1, pt2, cv.CV_RGB(0,0,255), 1)
                cv.PutText(color_image, str(i), pt1, font, cv.CV_RGB(255,0,255))
                cv.Circle(color_image, (center_x, center_y), 2, cv.CV_RGB(255,0,255), 2, 8, 0)

            #print "LEN ", len(self.tracked_objects)
            #if len(self.tracked_objects) > 0 and self.tracked_objects[0] is not None:
            #    #print "ENTRE"
            #    obj_vector = self.tracked_objects[0].movement_vector
            #    print "MVV LEN ", len(obj_vector)
            #    for index in range(0, len(obj_vector)-2):
            #        try:
            #            print "Index ", index, "len(obj_vector) ", len(obj_vector)
            #            cv.Line(color_image, obj_vector[index], obj_vector[index+1], cv.CV_RGB(0,255,0))
            #
            #        except: print "oops"

            #print "Iteration ", k, " Vector: ", vectors["1"]
            cv.ShowImage("Target", color_image)

            time_passed = time.time() - self.last_request
            request_threshold = 5
            if time_passed > request_threshold:
                self.send_occupancy()
                self.send_image(color_image)
            
            
            #Listen for ESC key
            c = cv.WaitKey(10)
            #c = cv.WaitKey(7) % 0x100
            if c == 27:
                break  

if __name__=="__main__":
    t = Target()
    t.run()
    
