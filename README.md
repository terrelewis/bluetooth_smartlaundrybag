# bluetooth_smartlaundrybag
Bluetooth implementation of laundry bag
Working of a smart laundry bag:
-The laundry bag will be equipped with a weight sensor to measure the weight of the clothes in the bag and a bluetooth module to communicate the data to the fabfresh mobile app.

Working of the smart laundry bag app:
-The app will run as a service. The reason for choosing a service over an activity is because we require the reading of the weight related data to be done in the background at regular intervals without the user having to do anything explicity.
-At regular intervals, the app will switch on the phone's bluetooth and try to connect to the laundry bag. On a successful connect, the app will fetch the latest readings from the laundry bag and if the weight crosses a predefined threshold value, a system notification will pop up in the user's phone  notifying him/her of the same. The user then has the option to place a pickup order or decline the option.
-If the connection does not happen, it is assumed that the user is not in the vicinity of the laundry bag and will hence switch off the bluetooth.

The purpose of this project is to simplify the process of placing a pickup order and lend an element of intelligence to the app.

While the other intern was responsible for developing the hardware prototype using an Arduino board, I was responsible to develop the app and integrate it into the main fabfresh app.

We successfully managed to connect the app with laundry bag and fetch the readings at regular intervals. 

Lifecycle of the service:
-Prior to using this feature, the user will have to initially pair the phone with the laundry bag explicitly. This has to be done only once. The user will then have to manually trigger the service once after which the service will get automatically triggered at regular intervals. This is achieved by the use of the AlarmManager class provided by Android. 
-The user also has the option of stopping the service post which the service won't get triggered automatically.
-Placing the pick up order requires making a RESTful API request and on receiving a success response, the order is placed.
