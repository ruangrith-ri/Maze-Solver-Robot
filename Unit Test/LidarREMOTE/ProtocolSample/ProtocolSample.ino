void setup() {
  Serial.begin(115200);
  Serial.flush();
}
int i = 0;
void loop() {
  /*
  Serial.print("[A:123]");
  Serial.print("[B:456]");
  Serial.print("[C:789]");
  Serial.print("[D:500]");


  Serial.print("[E:200]");
  Serial.print("[F:300]");
  Serial.print("[G:400]");*/
  Serial.print("[999:" + String(i++) + "]");
}
