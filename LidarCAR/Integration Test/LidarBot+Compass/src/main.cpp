#include <Arduino.h>
#include <M5Stack.h>
#include <esp_now.h>

//#include "AccessService.h"
#include "QMC5883LCompass.h"
#include "espnow.h"
//#include "iic.h"
#include "lidarcar.h"
//#include "rprtrack.h"

// I2C i2c;
Espnow espnow;
LidarCar lidarcar;
// AccessService service;
QMC5883LCompass compass;

extern const unsigned char gImage_logo[];

void Service(void *pvParameters);
void OnDataSent(const uint8_t *mac_addr, esp_now_send_status_t status);
void OnDataRecv(const uint8_t *mac_addr, const uint8_t *data, int data_len);

int flag = 0;

void setup() {
  M5.begin();

  Serial1.begin(230400, SERIAL_8N1, 16, 2);  // Lidar
  Serial2.begin(115200);                     // motor

  //! logo
  M5.Lcd.fillScreen(TFT_BLACK);
  M5.Lcd.pushImage(0, 0, 320, 240, (uint16_t *)gImage_logo);
  M5.Lcd.setCursor(200, 1, 4);
  M5.Lcd.printf("FRAM18");

  delay(2000);
  M5.Lcd.fillScreen(TFT_BLACK);

  //! esp
  espnow.BotInit();
  esp_now_register_recv_cb(OnDataRecv);
  esp_now_register_send_cb(OnDataSent);

  //! service
  // service.Init();

  M5.Lcd.setCursor(240, 220, 2);
  M5.Lcd.printf("mode");

  //! Motor
  lidarcar.Init();

  //! Compass
  compass.init();
  compass.setSmoothing(10, true);

  //! Camrea
  // i2c.master_start();

  //! Service
  // xTaskCreatePinnedToCore(Service, "Service", 40960, NULL, 5, NULL, 0);
}

void loop() {
  // espnow.BotConnectUpdate();    //For Change Remote
  lidarcar.MapDisplay();
  // lidarcar.ControlMode();

  compass.read();
  int azimuthAngle = compass.getAzimuth();
  M5.Lcd.setCursor(0, 60, 2);
  M5.Lcd.print("Azimuth : ");
  M5.Lcd.print(azimuthAngle);
  M5.Lcd.print("     ");

  if (digitalRead(37) == LOW) {
    flag++;
    if (flag >= 4) flag = 0;
    while (digitalRead(37) == LOW)
      ;
  }

  if (flag == 0) {
    // i2c.master_hangs();
    // esp_now_send(espnow.peer_addr, lidarcar.mapdata, 180);
    esp_err_t addStatus = esp_now_send(espnow.peer_addr, lidarcar.mapdata, 180);
    if (addStatus != ESP_OK) {
      lidarcar.ControlWheel(0, 0, 0);
    }
    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Remote");
  }

  if (flag == 1) {
    // i2c.master_hangs();
    esp_now_send(espnow.peer_addr, lidarcar.mapdata, 180);
    lidarcar.CarMaze();
    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Maze  ");
  }

  if (flag == 2) {
    // i2c.master_hangs();
    lidarcar.TrackControl();
    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Track  ");
  }

  if (flag == 3) {
    // i2c.master_recovery();
    // lidarcar.CarCamera();
    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Camera  ");
  }
}

void Service(void *pvParameters) {
  for (;;) {
    // service.Listen();
    vTaskDelay(2 / portTICK_RATE_MS);
  }
  vTaskDelete(NULL);
}

void OnDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {}

void OnDataRecv(const uint8_t *mac_addr, const uint8_t *data, int data_len) {
  if (espnow.OnBotRecv(mac_addr, data, data_len)) {
    return;
  }

  if ((data_len == 3) && (!flag)) {
    lidarcar.ControlWheel(data[0], data[1], data[2]);
  }

  if ((data_len == 4) && (!flag)) {
    lidarcar.LedShow();
  }
}
