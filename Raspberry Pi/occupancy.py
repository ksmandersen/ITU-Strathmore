import cv2.cv as cv
import cv2
import time
import datetime
import requests
import json
import thread

class Observation(object):
    def __init__(self):
        self.max_contours = 0

    def send(self):
        timestamp = long(datetime.datetime.utcnow().strftime('%s'))
        payload = {'occupancy': self.max_contours > 0, 'camera': 'CAM_01', "unixCaptureTimestamp": timestamp, "maxCountours": self.max_contours}
        headers = {'content-type': 'application/json'}
        url = "https://itu-strathmore-occupancy.appspot.com/_ah/api/occupancyPredictionAPI/v1/observation"
        r = requests.post(url, data=json.dumps(payload), headers=headers)
        print "Send request:"
        print payload

class OccupancyObserver(object):
    def __init__(self, camera_index):
        #self.camera = cv.CaptureFromCAM(camera_index)
        self.camera = cv.CaptureFromFile("/Users/ksma/Development/ITU-Strathmore/test_scripts/python/Video.mov")
        self.should_stop = False
        self.tick = 0
        self.request_threshold = 5

        self.initial_greyscale = None
        self.moving_average = None
        self.difference = None
        self.initial_temp = None
        self.current_observation = None

    def start(self):
        self.initial_state()
        self.tick = time.time()

        while True:
            self.next_observation()
            c = cv.WaitKey(10)
            if self.should_stop or c == 27:
                break

    def next_observation(self):
        image = cv.QueryFrame(self.camera)
        cv.Smooth(image, image, cv.CV_GAUSSIAN, 3, 0)
        cv.RunningAvg(image, self.moving_average, 0.020, None)

        # Convert the scale of the moving average.
        cv.ConvertScale(self.moving_average, self.initial_temp, 1.0, 0.0)

        # Minus the current frame from the moving average.
        cv.AbsDiff(image, self.initial_temp, self.difference)

        # Convert the image to greyscale.
        cv.CvtColor(self.difference, self.initial_greyscale, cv.CV_RGB2GRAY)

        # Convert the image to black and white.
        cv.Threshold(self.initial_greyscale, self.initial_greyscale, 90, 255, cv.CV_THRESH_BINARY)

        # Dilate and erode to get people blobs
        cv.Dilate(self.initial_greyscale, self.initial_greyscale, None, 18)
        cv.Erode(self.initial_greyscale, self.initial_greyscale, None, 10)

        storage = cv.CreateMemStorage(0)
        contour = cv.FindContours(self.initial_greyscale, storage, cv.CV_RETR_CCOMP, cv.CV_CHAIN_APPROX_TC89_KCOS)

        #cv

        contours_found = self.draw_contours(image, contour)
        cv.ShowImage("Target", image)

        if not self.current_observation:
            self.current_observation = Observation()
            self.current_observation.max_contours = contours_found

        if self.current_observation.max_contours < contours_found:
            self.current_observation.max_contours = contours_found
            print 'Higher occupancy'

        print contours_found

        should_send_observation = time.time() - self.tick > self.request_threshold
        if should_send_observation:
            try:
                thread.start_new_thread(self.current_observation.send, ())
            except:
                print "You suck"
            self.current_observation = None
            self.tick = time.time()

    def send_observation(self):
        self.current_observation.send()
        #self.current_observation = None
        #self.tick = time.time()

    def draw_contours(self, image, contour):
        max_contours = 0

        found = []
        while contour:
            bound_rect = cv.BoundingRect(list(contour))
            max_size = max(bound_rect[2], bound_rect[3])
            if max_size > 50:
                found.append(bound_rect)
                max_contours += 1
                pt1 = (bound_rect[0], bound_rect[1])
                pt2 = (bound_rect[0] + bound_rect[2], bound_rect[1] + bound_rect[3])
                cv.Rectangle(image, pt1, pt2, cv.CV_RGB(0,0,255), 1)

            contour = contour.h_next()

        for rect in found:
            pass
        return max_contours

    def initial_state(self):
        first_frame = cv.QueryFrame(self.camera)
        frame_size = cv.GetSize(first_frame)
        initial_image = cv.CreateImage(frame_size, 8, 3)
        self.initial_greyscale = cv.CreateImage(frame_size, cv.IPL_DEPTH_8U, 1)
        self.moving_average = cv.CreateImage(frame_size, cv.IPL_DEPTH_32F, 3)
        self.difference = cv.CloneImage(initial_image)
        self.initial_temp = cv.CloneImage(initial_image)
        cv.ConvertScale(initial_image, self.moving_average, 1.0, 0.0)
        # cv.ShowImage("Target", first_frame)

    def stop(self):
        self.should_stop = True


if __name__=="__main__":
    cv.NamedWindow("Target", 0)
    observer = OccupancyObserver(1)
    observer.start()