import requests
import datetime

timestamp = datetime.datetime.utcnow().strftime('%s')
#url = "http://localhost:8888/images/upload_url"
url = "https://itu-strath-occupancy.appspot.com/images/upload_url"

data = {'room-id': 'CAM_01', 'date': timestamp }
r = requests.get(url, params=data)

upload_url = str(r.json()['url'])

files = {'file': open('corridor_orig.jpg', 'rb')}
r2 = requests.post(upload_url, files=files)

