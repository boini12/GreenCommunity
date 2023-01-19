# GreenCommunity
GreenCommunity is an app that is developed using Kotlin and Android Studio. It is an assignment for the KAPK module at the OTH Regensburg.
The purpose of this app is to, create small communities that can share their homegrown produce (e.g that has been grown in a garden) with their neighborhood.

Users can sign-up for the app by using an email and a passwort (basic input). The Authentification process is done by using Firebase Authentification. This means all the user accounts
will be saved in a Firebase Authentification database. 

Users also have the chance to add additional information to their account. These include adding a username and a profile picture. This data is also saved in the same database as the other creditentials for the account.

After a successful sign-up the user will be automatically signed-in and can start using the application.

One of the Features includes creating ads and posting them. An ad can include a photo, a title, a description and a price (or is FREE). This data is stored on a Realtime Database from Firebase. Each post also saves the User UID (= the User ID from the Authentification Database) from the currently signed-in user, in order to be able to know which ad was created by whom.

-- Maybe insert an image of the Realtime Database structure here --

To get the location from the device, the Google API is used. This location gets automatically saved to a new ad when it is created. When the app is used for the first time, the user will get asked for permission to give the location services to the app. If the location services have not been turned on in the device, then the user will be redirected to the location settings.

## Key topics
The following topics will be covered in the following part:
* Installation

* How to use the app

* User Interface
    * Brief overview of the Activities with their corresponding layout
        * AdActivity
        * AdListActivity
        * HomeActivity
        * LoginActivity
        * LoginOrSignUpActivity
        * ProfileActivity
        * SignUpActivity
        * UserAdsActivity
    * Navigating between Activites

* Firebase
    * Realtime Database Structure
    * Code

* References





For the Login and the connection to Firebase I used the offical documentation from firebase.
For reference this is the link to this guide: https://firebase.google.com/docs/android/setup
