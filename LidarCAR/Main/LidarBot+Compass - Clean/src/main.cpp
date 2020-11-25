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

  // compass.setCalibration(-425, 1418, -1265, 566, -1, 1856);
  // compass.setCalibration(-435, 1448, -1308, 586, -1, 1748);
  // compass.setCalibration(-473, 1473, -1256, 596, -1, 1756);
  // compass.setCalibration(-561, 1495, -1342, 656, -1, 1780);
  // compass.setCalibration(-547, 1483, -1337, 620, -1, 1817);

  // compass.setCalibration(-1, 596, -1, 581, -1, 1725);

  // compass.setCalibration(-670, 1547, -1405, 778, -1, 1610);
  compass.setCalibration(-1270, 865, -1193, 885, -88, 93);

  compass.setSmoothing(10, true);

  beepBeep();
}

void loop() {
  lidarcar.MapDisplay();
  displayCompassInfo();
  modeSelection();
}