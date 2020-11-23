#include "QMC5883LCompass.h"

QMC5883LCompass compass;

void setup() {
  Serial.begin(9600);
  compass.init();
  compass.setSmoothing(10, true);
  
  /*
      call setSmoothing(STEPS, ADVANCED);

      STEPS     = int   The number of steps to smooth the results by. Valid 1 to 10.
                        Higher steps equals more smoothing but longer process time.

      ADVANCED  = bool  Turn advanced smmothing on or off. True will remove the max and min values from each step and then process as normal.
                        Turning this feature on will results in even more smoothing but will take longer to process.

  */
}

void loop() {s
  compass.read();
  
  Serial.println(compass.getAzimuth());
  delay(250);
}
