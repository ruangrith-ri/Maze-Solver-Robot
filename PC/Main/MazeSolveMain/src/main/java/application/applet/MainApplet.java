package application.applet;

import application.service.SerialEventBus;
import processing.core.PApplet;

public class MainApplet extends PApplet {

    SerialEventBus serialEventBus;
    int i = 0;

    @Override
    public void settings() {
        size(300, 300, P3D);
    }

    @Override
    public void setup() {
        serialEventBus = new SerialEventBus("COM8",115200);
    }

    @Override
    public void draw() {
        background(255, 0, 0);

        println(serialEventBus.showAll());

        if(serialEventBus.readNonContain("wallN").equals("1")) {
            serialEventBus.send("direction", "N");
        }
    }
}
