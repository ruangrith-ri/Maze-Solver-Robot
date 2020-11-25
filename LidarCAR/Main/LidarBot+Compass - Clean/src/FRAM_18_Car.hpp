#include <Arduino.h>
#include <M5Stack.h>
#include <esp_now.h>

#include "QMC5883LCompass.h"
#include "espnow.h"
#include "lidarcar.h"

static Espnow espnow;
static LidarCar lidarcar;
static QMC5883LCompass compass;

static uint8_t azimuthAngle[2];

extern const unsigned char gImage_logo[];
int flag = 0;

void beepBeep() {
  M5.Speaker.beep();
  delay(100);
  M5.Speaker.mute();
  delay(100);
  M5.Speaker.beep();
  delay(200);
  M5.Speaker.mute();
}

void spreadScreen() {
  M5.Lcd.fillScreen(TFT_BLACK);
  M5.Lcd.pushImage(0, 0, 320, 240, (uint16_t *)gImage_logo);
  M5.Lcd.setCursor(70, 200, 4);
  M5.Lcd.printf("FRAM 18 Library");
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

void modeSelection() {
  if (digitalRead(37) == LOW) {
    flag++;
    if (flag >= 2) flag = 0;
    while (digitalRead(37) == LOW)
      ;
  }

  if (flag == 0) {
    esp_err_t addStatus = esp_now_send(espnow.peer_addr, lidarcar.mapdata, 180);
    // esp_err_t addCompass = esp_now_send(espnow.peer_addr, azimuthAngle, 2);
    if (addStatus != ESP_OK) {
      // lidarcar.ControlWheel(0, 0, 0);
    }

    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Remote");
  }

  if (flag == 1) {
    esp_now_send(espnow.peer_addr, lidarcar.mapdata, 180);
    // esp_now_send(espnow.peer_addr, azimuthAngle, 1);
    lidarcar.CarMaze();
    M5.Lcd.setCursor(240, 0);
    M5.Lcd.printf("Maze  ");
  }
}

static uint16_t azimuth16 = 0;

void displayCompassInfo() {
  compass.read();

  azimuth16 = compass.getAzimuth();

  azimuthAngle[0] = azimuth16;
  azimuthAngle[1] = azimuth16 >> 8;
}