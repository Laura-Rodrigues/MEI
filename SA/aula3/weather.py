import webbrowser
import requests
import time 
import firebase_admin
from firebase_admin import db

default_app = firebase_admin.initialize_app()


ref = db.reference("/")

cred_object = firebase_admin.credentials.Certificate('....path to file')
default_app = firebase_admin.initialize_app(cred_object, {
	'databaseURL': "https://weather-2b1e0-default-rtdb.firebaseio.com/"
	})
  
city = "Braga"
link = f"https://api.openaq.org/v1/latest?city={city}"

headers = {"Accept" : "application/json"}

while (True): 
    print ("WEBSITE IS OPENING :D") 
    response = requests.get(link, headers=headers)
    print(response.content)

    

    webbrowser.open(link)
    time.sleep(5) 
