#pragma once

#include <Arduino.h>

class Foo {
 private:
  int x;

 public:
  Foo(int x) { this->x = x; }
  int getX() { return x; };
};

static Foo Bus = Foo(100);