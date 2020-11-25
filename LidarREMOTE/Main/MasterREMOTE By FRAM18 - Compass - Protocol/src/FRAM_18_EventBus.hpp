/*
BY FIBO MASTER STUDENT (FRAM18)
*/

#pragma once

#include <Arduino.h>

#include "HashMap.h"

namespace SerialEventBus {

const int HASH_SIZE = 512;

HashMap<String, String, HASH_SIZE> hashMap =
    HashMap<String, String, HASH_SIZE>();

TaskHandle_t SerialTask;

void SerialTaskRunner(void* pvParameters) {
  String inputBuffer;

  while (1) {
    if (Serial.available()) {
      inputBuffer = Serial.readStringUntil('\n');

      int first = inputBuffer.indexOf('[');
      int middle = inputBuffer.indexOf(':');
      int last = inputBuffer.indexOf(']');

      if (first < middle && middle < last) {
        String topic = inputBuffer.substring(first + 1, middle);
        String content = inputBuffer.substring(middle + 1, last);

        hashMap[topic] = content;
      }
    }
  }
}

void send(String topic, String content) {
  Serial.println('[' + topic + ':' + content + ']');
  hashMap[topic] = content;
}

String read(String topic) { return hashMap[topic]; }

String readNonContain(String topic) {
  String buffer = hashMap[topic];
  hashMap[topic] = "";
  send(topic, hashMap[topic]);

  return buffer;
}

void begin() {
  xTaskCreatePinnedToCore(SerialTaskRunner, "SerialTask", 6000, NULL, 0,
                          &SerialTask, 1);
}

}  // namespace SerialEventBus