import time
import os
import telepot
from telepot.loop import MessageLoop
import pickle
import requests
import schedule
from datetime import datetime
import pandas as pd
import imgkit
import json
from bs4 import BeautifulSoup as BS
from io import BytesIO
from zipfile import ZipFile



config = imgkit.config(wkhtmltoimage='./wkhtmltoimage.exe')    #change your path

try:
    adminKeyFile = open('adminKey.txt','r')
    adminKey = adminKeyFile.read()
except FileNotFoundError:
    adminKey = 'admin'

adminCommand = set()
waiting_for_adminKey = set()
url='https://api.data.gov.sg/v1/environment/psi'
  
def update():
	
    haze_photo = open('./haze_status.png','rb')
    dengue_photo = open('./dengue_status.png', 'rb')
        
def crawling_dengue_data(url='https://data.gov.sg/dataset/e7536645-6126-4358-b959-a02b22c6c473/download'):
    global today
    resp = requests.get(url).content
    zipfile = ZipFile(BytesIO(resp))
    
    dengue_data = json.load(zipfile.open('dengue-clusters-geojson.geojson', 'r'))
    dengue_status = {'LOCATION': [], 'CASE_SIZE': []}
    today = datetime.today().replace(microsecond=0)
    pickle.dump(today, open('./today.obj','wb')) 

    
    for feature in dengue_data['features']:
        soup = BS(feature['properties']['Description'], 'html.parser')
        iterator = soup.table.children
        while True:
            child = next(iterator)
            if child.th.string == 'LOCALITY':
                dengue_status['LOCATION'].append(child.td.string)
                break
        while True:
            child = next(iterator)
            if child.th.string == 'CASE_SIZE':
                dengue_status['CASE_SIZE'].append(child.td.string)
                break
    dengue_status = pd.DataFrame(dengue_status)
    text_file = open('./dengue_status.html', 'w+') 

    text_file.write(css)
    text_file.write(dengue_status.to_html())
    text_file.close()
    imgkit.from_file("./dengue_status.html", "./dengue_status.png", options=imgkitoptions) 
    
        

schedule.every().day.do(update)
schedule.every().day.do(crawling_dengue_data)

css = """
<style type=\"text/css\">
table {
color: #333;
font-family: Helvetica, Arial, sans-serif;
width: 640px;
border-collapse:
collapse; 
border-spacing: 0;
}
td, th {
border: 1px solid transparent; /* No more visible border */
height: 30px;
}
th {
background: #DFDFDF; /* Darken header a bit */
font-weight: bold;
text-align: center;
}
td {
background: #FAFAFA;
text-align: center;
}
table tr:nth-child(odd) td{
background-color: white;
}
</style>
"""

imgkitoptions = {"format": "png",
                 "crop-w": '700'}


global haze_response
global today

while 1:
    if not os.path.isfile('./dengue_status.png'):  
    
        crawling_dengue_data()
        today = pickle.load(open('./today.obj','rb'))
    else: 
    	today_file = pickle.load(open('./today.obj','rb'))
    	today = datetime.today()
    	if (today.year, today.month, today.day) != (today_file.year, today_file.month, today_file.day):
    		crawling_dengue_data()
    	else:
    		today = today_file
    haze_response = requests.get(url).json()
    haze_status = {}
    haze_status = {'POLUTION_INDEX_TYPE': [],'WEST': [], 'NATIONAL': [], 'EAST': [], 'CENTRAL': [], 'SOUTH': [], 'NORTH': []}
    for index_type, info in haze_response['items'][0]['readings'].items():
        haze_status['POLUTION_INDEX_TYPE'].append(index_type.upper())
        for region, index_val in info.items():
            haze_status[region.upper()].append(index_val)
    haze_status = pd.DataFrame(haze_status)
    haze_status = haze_status[['POLUTION_INDEX_TYPE', 'WEST', 'NATIONAL', 'EAST', 'CENTRAL', 'SOUTH', 'NORTH']]
    text_file = open('./haze_status.html', 'w+') 
    text_file.write(css)
    text_file.write(haze_status.to_html())
    text_file.close()
    
    imgkit.from_file("./haze_status.html", "./haze_status.png", options=imgkitoptions)
    schedule.run_pending()
    time.sleep(43200)
        
            

    
