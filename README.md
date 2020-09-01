# BLE_Connectivity

- Purpose built android application for a personal desk lighting project.
- Utilizes Arduino IOT 33 to facilitate WS2812B addressable LED programming and communications with Android application.
- Arduino code is located in separate project (https://github.com/1ryan3/ArduinoBLE_LEDController), and is in need of more development as it is more for testing right now.
- Currently the application allows for 5 channels of control, but could easily expand to the support the full 8 PWM outputs of the Arduino IOT 33.


# Future Plans....

- **Brightness:**   _likely to be another field in the colorSet characteristic byte array, so as simple as creating a new slider EDIT: Turns out this would likely be a restructuring of how different color values are set. Absolute brightness is a bit more complicated than "turning a dial"_
- **Current color value display:**    _Done_
- **Color visualizer??:**   _Done. Swapped to a dynamic color selection (continuously updated from app to device). Not sure if this is a good value add, but it is done._
- **More granular settings:**   _control over individual LEDs, pre-programmed patterns, dynamic patterns (ie. the classic RGB rainbow shift)_
- **Local storage of channel settings:**  _Currently create a basic struct to hold this information per LED group. I think this will be key in implementing more interesting/visually dynamic configurations. _

- **Per LED per string configuration** _From a UX perspective I am not exactly sure how I want to accomplish this. In my head I see the user (myself at this point), being able to visually select individual LEDs in a strip and color them, but that might be a bit much. 
                 
