#include "FRAM_18_Remote.hpp"

void setup() {
  M5.begin();

  spreadScreen();
  delay(2000);

  M5.Lcd.fillScreen(TFT_BLACK);

  keyboard.Init();
  espnow.RemoteInit();
  esp_now_register_recv_cb(OnDataRecv);

  beepBeep();
}

void loop() {
  analogControl();
  displayCarInfo();
  MapDisplay();
  flashLED();

  // int angle = getAzimuthAngle();
}