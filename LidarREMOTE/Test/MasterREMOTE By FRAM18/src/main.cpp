#include "FRAM_18_Remote.hpp"

/*----------------------------TEST JEENO----------------------------------*/
// const unsigned int HASH_SIZE = 512;
// storage
// static HashMap<String, String, HASH_SIZE> hashMap =
// HashMap<String, String, HASH_SIZE>();
/*----------------------------TEST JEENO----------------------------------*/

void setup() {
  M5.begin();

  spreadScreen();
  delay(2000);

  M5.Lcd.fillScreen(TFT_BLACK);

  setEspNowTask();
  beepBeep();

  /*----------------------------TEST JEENO----------------------------------*/

  setSerial();
  /*
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

    Serial.println(serialBus.getX());
    */
  /*-----------------------------TEST JEENO----------------------------------*/
}

int i = 0;

void loop() {
  //  Serial.println("[" + String(i) + ":" + String(distance[i]) + "]");

  Serial.println("[A:N]");
  Serial.println("[B:456]");
  Serial.println("[C:789]");
  Serial.println("[D:500]");

  Serial.println("[E:200]");
  Serial.println("[F:300]");
  Serial.println("[G:400]");
  Serial.println("[H:" + String(i++) + "]");
}