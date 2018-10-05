# mis_final-project

This application is a prototype for a local landmark based navigation using a smartwatch. 
It has to be seen as a proof of concept.

# Installation instructions

1. Install Android Studio on your system and everything else needed to develop an Android Application.
2. Clone this repository.
3. Open the 'mis_final-project'-project with Android Studio.
4. Add a 'secrets.xml' containing your API-key to: mobile -> res -> values
4.1 the 'secrets.xml' should look like:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="com.google.android.geo.API_KEY"> YOUR_KEY_HERE </string>
</resources>
```
5. Build both mobile and wear applications.
6. Pair the smartwatch (best a LG G Watch) with the smartphone.
7. Enable the developer options on both devices.
8. Enable ADB-Debugging on both devices.
9. Install the applications on the devices.

# Usage
1. Make sure to have a bluetooth and a internet connection as well as access to location services.
2. Start the No Way Navigation on the smartphone as well as on the smartwatch.
3. On the smartphone, first pick a target by pressing the 'PICK TARGET'-button.
4. After picking a target press the 'START NAVIGATION ON WATCH'-button.
5. The smartwatch should now show 4 red circles and other symbols.
6. If the north pointer is not pointing towards north recalibrate the sensor by making 8-shape movements with the watch.
