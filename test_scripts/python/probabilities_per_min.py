from __future__ import division
import requests
import datetime

url = "https://itu-strathmore-occupancy.appspot.com/_ah/api/occupancyPredictionAPI/v1/observations?camera=CAM_02&fields=items(captureDate%2Cday%2CmaxContoursDetected%2Coccupancy%2CtimeOfDay)"

r = requests.get(url)

observations = r.json()['items']

sorted_observations = sorted(observations, key=lambda k: k['captureDate']) 
groupped_by_min = {}
for obs in sorted_observations:
	minute = datetime.datetime.strptime(obs['captureDate'], "%Y-%m-%dT%H:%M:%S.000Z").time().strftime("%M") 
	if str(minute) in groupped_by_min:
		occupancy = 1 if obs['occupancy'] else 0
		obs_per_min = groupped_by_min[str(minute)]
		obs_per_min['observations'] += 1
		obs_per_min['occupied_observations'] += occupancy
		obs_per_min['probability'] = obs_per_min['occupied_observations'] / obs_per_min['observations']
	else:
		occupancy = 1 if obs['occupancy'] else 0
		groupped_by_min[str(minute)] = { 'probability': occupancy, 'observations': 1, 'occupied_observations': occupancy}

sorted_result = sorted(groupped_by_min.items())	#This is a tuple, not a dictionary

print sorted_result
print groupped_by_min
#Do whatever with groupped_by_min or sorted_result