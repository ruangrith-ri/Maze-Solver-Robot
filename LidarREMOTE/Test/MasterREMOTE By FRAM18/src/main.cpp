#include "FRAM_18_Remote.hpp"

void setup() {
  M5.begin();

  spreadScreen();
  delay(2000);

  M5.Lcd.fillScreen(TFT_BLACK);

  espnow.RemoteInit();
  esp_now_register_recv_cb(OnDataRecv);
  keyboard.Init();

  SerialEventBus::begin();

  beepBeep();
}

int i = 0;

void loop() {
  displayCarInfo();
  MapDisplay();
  analogControl();
  flashLED();

  // SerialEventBus::send("A", "N");
  // SerialEventBus::send("B", String(456));
  // SerialEventBus::send("ESP", String(i++));
  // SerialEventBus::send("ESP2", String(i++));
  // SerialEventBus::send("D", "Hello");

  M5.Lcd.setCursor(0, 40, 2);
  M5.Lcd.print("Test Read : ");
  M5.Lcd.print(SerialEventBus::read("PC"));

  M5.Lcd.setCursor(0, 60, 2);
  M5.Lcd.print("Test Send : ");
  M5.Lcd.print(SerialEventBus::read("ESP"));

  M5.Lcd.setCursor(0, 80, 2);
  M5.Lcd.print("Test Send : ");
  M5.Lcd.print(SerialEventBus::read("ESPfgkjnojfgg") == ""
                   ? "NO DATA"
                   : SerialEventBus::read("ESPfgkjnojfgg"));

  Serial.println("N/A Topic : " + (SerialEventBus::read("ESPfgkjnojfgg") == ""
                                     ? "NO DATA"
                                     : SerialEventBus::read("ESPfgkjnojfgg")));
}