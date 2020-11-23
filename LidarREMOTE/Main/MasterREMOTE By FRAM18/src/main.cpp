#include "FRAM_18_Remote.hpp"
#include "HashMap.h"

/*----------------------------TEST JEENO----------------------------------*/
const byte HASH_SIZE = 10;
// storage
static HashMap<String, String, HASH_SIZE> hashMap =
    HashMap<String, String, HASH_SIZE>();
/*----------------------------TEST JEENO----------------------------------*/

void setup() {
  M5.begin();

  spreadScreen();
  delay(2000);

  M5.Lcd.fillScreen(TFT_BLACK);

  keyboard.Init();
  espnow.RemoteInit();
  esp_now_register_recv_cb(OnDataRecv);

  beepBeep();

  /*----------------------------TEST JEENO----------------------------------*/
  hashMap["name"] = "N";
  hashMap["test"] = (String)200;

  String a = hashMap["name"];
  int b = hashMap["test"].toInt();
  String c = hashMap["test"];

  Serial.println("----");
  Serial.println(a);
  Serial.println("----");
  Serial.println(b);
  Serial.println("----");
  Serial.println(c);
  /*-----------------------------TEST JEENO----------------------------------*/
}

void loop() {
  analogControl();
  displayCarInfo();
  MapDisplay();
  flashLED();
}