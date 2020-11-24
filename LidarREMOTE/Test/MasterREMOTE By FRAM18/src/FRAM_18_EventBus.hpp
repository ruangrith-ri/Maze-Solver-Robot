#pragma once

#include <Arduino.h>

#include "HashMap.h"

static const int HASH_SIZE = 10;
HashMap<String, String, HASH_SIZE> hashMap =
    HashMap<String, String, HASH_SIZE>();
/*
class EventBus {
 private:
  EventBus();

 public:
  EventBus();
};
*/
// static EventBus serialBus = EventBus();

TaskHandle_t SerialTask;

void SerialTaskRunner(void* pvParameters) {
  String command;

  while (1) {
    if (Serial.available()) {
      command = Serial.readStringUntil('\n');

      M5.Lcd.setCursor(70, 200, 4);
      M5.Lcd.print(command);
    }
  }
}

void setSerial() {
  xTaskCreatePinnedToCore(SerialTaskRunner, "SerialTask", 30000, NULL, 1,
                          &SerialTask, 1);
}