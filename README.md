# App Components:

## Train:
* select training parameters like:
  * amount of words
  * Lesson
  * KnowledgeLevel of words to train
* start the training for the currently selected language

## Library:
* Manages the Library for the currently selected language
* swipe to delete entries
* click to update
* FAB to add new words
* add/update words:
 * foreign word
 * native word
 * knowledge Level

## Lessons:
* Manages the Lessons for the currently selected language
* swipe to delete entries
* click to update
* FAB to add new lessons
* add/update lessons:
 * Lesson name
 * Lesson description
 * mapping of words to lesson (words can be in multiple lessons)
 
## Statistics:
* Displays overview over word count for current language by knowledge level
* Displays trainging activity over last 14 days for all languages

## Settings:
* Manage languages:
 * select
 * add new
 * delete
* Set the default knowledge Level for new words.

## Widget:
* Different motivational output depending on when the last training was
* Proposes lesson to train (worst lesson for selected language)
* click on widget starts CuLa

# Disable dummy prefil:
* For testing purposes the Room Database is prefilled with some entries for the different tables.
* To disable that:
 * open CulaRepository.java (in package com.sliebald.cula.data) and comment out the following line in the constructor:
  * setDebugState();
 * Afterwards the App is setup without any prefils.
 * only enabled for debug version, release version should ship without dummy prefill

# google services used:
 * room database for persistence
 * firebase crashlytics to get error reports on critical crashes
 * firebase analytics to analyze defaults + starting/finishing of training sessions

# Setup firebase:
* create a new firebase project:
  * follow firebase instructions on https://console.firebase.google.com/
  * create new project
  * store google-services.json to cula/app/google-services.json
* enable crashlytics:
  * in the firebase console of the project click on Crashlytics
  * should directly work after starting the app once
 * configure google analytics keys in the firebase console:

  
  
# Initial Design can be found here (some parts are not implemented yet, some are additional):
* https://github.com/SLiebald/Capstone-Project
