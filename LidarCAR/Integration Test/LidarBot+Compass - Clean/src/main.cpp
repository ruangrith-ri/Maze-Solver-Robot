#include "FRAM_18_Car.hpp"

void setup() {
  M5.begin();

  Serial1.begin(230400, SERIAL_8N1, 16, 2);  // Lidar
  Serial2.begin(115200);                     // motor

  spreadScreen();
  delay(2000);
  M5.Lcd.fillScreen(TFT_BLACK);

  espnow.BotInit();
  esp_now_register_recv_cb(OnDataRecv);
  esp_now_register_send_cb(OnDataSent);

  M5.Lcd.setCursor(240, 220, 2);
  M5.Lcd.printf("mode");

  //! Motor
  lidarcar.Init();

  compass.init();
  compass.setSmoothing(10, true);

  beepBeep();
}

void loop() {
  lidarcar.MapDisplay();
  displayCompassInfo();
  modeSelection();
}