![picture alt](http://ec2-18-188-133-222.us-east-2.compute.amazonaws.com/static/img/pap.4fb43f2.png "Pic-a-Pup Logo")

Pic-a-Pup is a multiplatform application targeted towards dog lovers and individuals interested in learning more about dogs and services created in Temple University's Projects In Computer Science capstone course. It's as simple as point and click with your phone’s camera or uploading an image of that new furry friend and Pic-a-Pup handles the rest. Pic-a-Pup has its own powerful neural network built into its Pic-a-Breed feature to identify the breed of the dog in real time from the uploaded image. Once the breed has been identified, the Pic-a-Pup presents users with helpful information about the dog breed and, if available, matching dogs of the found breed that are available for adoption in their area. If a dog goes missing, the owner can report that and if the dog is found by another user, she can scan the lost dog's collar and will be given information to contact the dog’s owner in our Pic-a-Collar feature (available in the iOS and Android application). The Pic-a-Park feature provides users with the ability to search for dog parks, pet stores, and veterinaries within a defined radius of their current location. The web application also provides the Pic-a-Service feature which allows users to register with the application and provide dog services, such as, dog walking and sitting.

# Requirements

### General requirements to run the Pic-a-Pup application:
#### Mobile device:
* Android OS 7+ or iOS 11+ 
* Camera 
* Gallery access 
* Network connection 
* Global Positioning System (GPS)

#### Image recognition model:
* Python and Tensorflow installation 
* Training dataset of at least 15,000 labeled images 
* Labeled evaluation dataset for measuring accuracy 
* Measured success rate of 75% or higher for at least 25 breeds 

---

## Running the Android Application

The application is not available on the Google Play Store so you will be required to sideload the application's .apk file to run it.  In order to sideload an application visit your device's security settings.

Install the .apk file. After installation, locate the app labeled Pic-a-Pup in your device's application drawer and click to launch.

Within the application, the bottom navigation drawer will provide you with links to each of the application's features.

## Running the iOS Application

## Running the Web Application

### Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run e2e tests
npm run e2e

# run all tests
npm test

# View Web Running Instance
http://ec2-18-188-133-222.us-east-2.compute.amazonaws.com/
```

## Backend Server

This server is was implemented for an Ubuntu 16.04.04 LTS system. We used an AWS EC2 instance.
We implement using a Flask, Uwsbi, and Nginx stack. The language we choose is Python 2.7.12. 
All python dependencies are enumerated in the requirements.txt file.

### Features

The job of this server is to service requests from clients sent using HTTP POST. When it receives
an image URL, it will download the image from that URL and send it through our dog breed identifier 
AI. If the AI returns a dog breed with high enough confidence the server will then query wikipedia
to get some basic information on that breed. Finally it will query PetFinder in order to find an 
available dog of that breed in the nearby area. That data will then be encapsulated into one JSON 
object and returned to the client.

### Installation

	1. git clone http://github.com/YoungP036/PAP_backend.git
	2. sudo apt-get install python python-pip nginx-full uwsgi
	3. cd /path/to/PAP_backend	
	4. sudo pip install -r requirements.txt

### Start Server

	1. cd /path/to/PAP_backend
	2. service nginx restart
	3. uwsgi uwsgi.ini
	4. cat *.log and examine the most recent entries to verify proper server startup

### Stop Server

	1. cd /path/to/PAP_backend
	2. uwsgi --stop app.pid
	3. fuser -k 3033/tcp

### Server Requests

Requests from clients to this server should be made using HTTP POST. In the data section should be two 
key-value pairs, 'url':'www.website.com' and 'location':'<zipcode>'. The URL must be to an image file 
of type png or jpg. 
In response the server will send a JSON object with a subset of the following keys:
'breed', 'prob', 'contact', 'name', 'sex', 'age', 'size', 'shelter_contact', 'photos', 'breed_info',
'petfinder_error', 'model_error', 'wikipedia_error', and 'server_error'.
  
  ---

## Pic-a-Pup's Web and iOS Application Github Repositories

<a href="https://github.com/drwitteck/Pic-a-Pup">Pic-a-Pup Web Application</a>

<a href="https://github.com/illphil610/Pic.a.Pup">Pic-a-Pup iOS Application</a>
