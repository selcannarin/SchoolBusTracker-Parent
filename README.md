<br/>
<p align="center">
  <a href="https://github.com/selcannarin/SchoolBusTracker-Parent">
    <img src="https://github.com/selcannarin/SchoolBusTracker-Parent/assets/72921635/3c38443f-764f-439f-96e2-f3994aa284a4" alt="Logo" width="100" height="100">
  </a>

  <h3 align="center">School Bus Tracker - Parent</h3>

  <p align="center">
    It is the application of the project called School Bus Tracking for parents who want to follow their students' school buses. It cooperates with the <a href="https://github.com/selcannarin/SchoolBusTracker-Driver">School Bus Tracker - Parent</a> application, provides communication between parents and drivers.
    <br/>
    <br/>
    <a href="https://github.com/selcannarin/SchoolBusTracker-Parent"><strong>Explore the docs »</strong></a>
    <br/>
    <br/>
  </p>
</p>

![Downloads](https://img.shields.io/github/downloads/selcannarin/SchoolBusTracker-Parent/total) ![Contributors](https://img.shields.io/github/contributors/selcannarin/SchoolBusTracker-Parent?color=dark-green) ![Stargazers](https://img.shields.io/github/stars/selcannarin/SchoolBusTracker-Parent?style=social) 

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Usage](#usage)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)

## About The Project

This application, for parents who want to track their students' shuttles, was designed to optimize student transportation processes, increase safety and provide better communication with drivers by working in collaboration with <a href="https://github.com/selcannarin/SchoolBusTracker-Driver">School Bus Tracker - Driver</a> application.

 <h4><strong>Main Features of the Project: </strong></h4>

* <strong>Driver Tracking:</strong> Provides instant viewing of the driver's location via Google Maps. With automatic notifications of the driver, it is notified whether the student is on the bus or not. Parents can easily access driver information.

* <strong>Effective Communication with Driver:</strong> Provides powerful communication between drivers and parents with automatic notifications, so parents can be sure that their children are safe.

* <strong>Student Information Management:</strong> Parents can easily see or correct their students' information, so itinerary changes can be made easily.

## Built With

Android: Clean Architecture, MVVM

Dagger Hilt (Dependency Injection)

Google Play Services (Location, Maps)

Firebase (Firestore, Authentication, Storage, Cloud Messaging)

Coroutines (Kotlin Asynchronous Programming)

Glide (Image Loading)

GIF Support

Android Navigation Components

## Usage

![AUTHENTICATION-1](https://github.com/selcannarin/SchoolBusTracker-Parent/assets/72921635/1135120f-7840-4607-8f48-10a48b4e223b)
![AUTHENTICATION-2](https://github.com/selcannarin/SchoolBusTracker-Parent/assets/72921635/2a21f39d-1190-4320-8ff7-bf3430c29a75)


## Getting Started

To get a local copy up and running follow these simple example steps.

### Prerequisites

You can get a free Google Maps API key with the help of this [document](https://developers.google.com/maps/documentation/android-sdk/get-api-key).

### Installation

1. [Get a free Map API Key](https://console.cloud.google.com/project/_/google/maps-apis/credentials?utm_source=Docs_CreateAPIKey&utm_content=Docs_maps-android-backend&_gl=1*1gil097*_ga*MTA2MzY5OTU2LjE2ODEzNDA0Mzc.*_ga_NRWSTWS78N*MTY5NDM3MTg3MS4xMS4xLjE2OTQzNzE5OTIuMC4wLjA.) 

2. Clone the repo

```sh
git clone https://github.com/selcannarin/SchoolBusTracker-Parent.git
```

3. Enter your API in `strings.xml`

```JS
 <string name="google_api_key">YOUR API KEY</string>
```
