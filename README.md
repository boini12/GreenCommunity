<h1 align="center">Welcome to the GreenCommunity 🌱</h1>
GreenCommunity is an app that is developed using Kotlin and Android Studio. It is an assignment for the KAPK module at the OTH Regensburg.

The purpose of this app is, to create small communities that can share their homegrown produce (e.g that has been grown in a garden) with their neighborhood.

Users can sign-up for the app by using an email and a passwort (basic input). The Authentification process is done by using Firebase Authentification. This means all the user accounts
will be saved in a Firebase Authentification database. 

Users also have the chance to add additional information to their account. This includes adding a username and a profile picture. This data is also saved in the same database as the other creditentials for the account.

After a successful sign-up the user will be automatically signed-in and can start using the application.

One of the Features includes creating ads and posting them. An ad can include a photo, a title, a description and a price (or is FREE). This data is stored on a Realtime Database from Firebase. Each post also saves the User UID (= the User ID from the Authentification Database) from the currently signed-in user, in order to be able to know which ad was created by whom. In addition to that, the location from the device will also be saved. This is important to later only display the ads in a 5km range.

To get the location from the device, the Google API is used. This location gets automatically saved to a new ad when it is created. When the app is used for the first time, the user will get asked for permission to give the location services to the app. If the location services have not been turned on in the device, then the user will be redirected to the location settings.

## Key topics
The following topics will be covered in the following part:
* Installation

* How to use the app
  * Sign up
  * Login
  * Exploring ads
  * Posting ads
  * Edit ads

* UI
    * Brief overview of the Activities with their corresponding layout
        * HomeActivity
        * AdActivity
        * AdListActivity  
        * LoginActivity
        * LoginOrSignUpActivity
        * ProfileActivity
        * SignUpActivity
        * UserAdsActivity
    * Navigating between Activites

* Firebase
    * Realtime Database Structure
    * Code
    
* Messanger
  
* Tests

* References

## Installation
For the app a minimum SDK of 30 is set. The target SDK is set to 32. This means Android phones running Android 29 and lower won't be able to open this app. 

For the testing purposes of the application an Emulator in Android Studio was used. This was a Pixel 4 running Android 31.

During the course of developing this app other Emulators running Android 30 and Android 32 were also tested, both were Pixel devices again.

## How to use the app
After starting the application, the user will be promted with the HomeActivity. The user can press the button in the middle of the screen. If no user is logged in, then this Activity will redirect the user to another Activity where they can decide wheather they want to login (Account already exits) or want to sign-up for this app.

### Signing up
The sign-up process involves entering data, that will be stored in a Firebase Authentification database. 

<ins>This includes:<ins>
- Username*
- E-Mail*
- Password*
- Profile picture (-> if none uploaded, then default picture will be used)

The ones marked with * are necessary to successfully sign up.

### Login
If the user already has an account, the he can simply login using the E-Mail and passord.

Either way the user will get redirected to the HomeActivity again after a successful login or sign-up.

### Exploring ads
One of the main features that the user can use is to look for ads in their area. The user just has to press the "Explore ads in your area" on the HomeActivity to start looking for ads that are in a 5km radius to their current location.

### Posting ads
Another major feature of this ad, is posting ads. Logged-in users can create new ads, by pressing the plus icon on the top-right in the AdListActivity. 

To create a new ad the user has to provide a bit of information.

<ins>This includes:<ins>
- Title*
- Description*
- Picture (-> if none uploaded, the default picture will be used)
- Price*
- Free (CheckBox)*

The ones marked with * are necessary to successfully post an ad. Keep in mind, that the price and free checkbox don't have to be both clicked or supplied. Only one of them is necessary. For example, if the free checkbox is checked, the price will be automatically set to 0.0€. And if a price is entered, the free checkbox won't be clickable anymore.

### Edit ads
Users can edit their already posted ads. First they need to navigate to in the Navigation Drawer to the "Your ads" option, to see a list of their ads. By clicking on one of these ads, another activity is started where changes can be made and saved.

All the data can be changed.

<ins>This includes:<ins>
- Title
- Description
- Picture (-> if none uploaded, the default picture will be used)
- Price
- Free (CheckBox)

Ads can also be deleted here, by clicking the delete button on the top-right of this activity.

### Message another user
When a user has clicked on a ad in their area, they have the option to message the owner of this ad.

## UI
In this section, a brief overview of all the activties can be found with a major focus of their design.

### HomeActivity
This is the current design of the HomeActivity

<img src="images/HomeActivity.png"
     alt="HomeActivity"
     style="float: left; margin-right: 10px;" />

<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
The Homeactivity consist of one major functionality. This is the "Explore ads in your area" in the middle of the screen. This button redirects the user to the AdListActivity, if a user is logged-in, or to the LoginOrSignUpActivity, if no user is logged-in yet.






## References
For the Login and the connection to Firebase I used the offical documentation from firebase.
For reference this is the link to this guide: https://firebase.google.com/docs/android/setup
