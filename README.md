# BLE_Connectivity

- Purpose built android application for a personal desk lighting project.
- Utilizes Arduino IOT 33 to facilitate WS2812B addressable LED programming and communications with Android application.
- Arduino code is located in separate project, and is in need of more development as it is more for testing right now.
- Currently the application allows for 5 channels of control, but could easily expand to the support the full 8 PWM outputs of the Arduino IOT 33.


# Future Plans....

- **Brightness:**   _likely to be another field in the colorSet characteristic byte array, so as simple as creating a new slider_
- **Current color value display:**    _another easy one, but definitely a feature I would want_
- **Color visualizer??:**   _If I want to go with a control flow of "Set Color in App -> Click button to send to Arduino" then it would be useful to show the user what color their values will generate_
- **More granular settings:**   _control over individual LEDs, pre-programmed patterns, dynamic patterns (ie. the classic RGB rainbow shift)_
- **Local storage of channel settings:**  _Store color, brightness and other settings like pre-programmed color patterns on arduino for persistence when LEDS are unpowered_
                 
