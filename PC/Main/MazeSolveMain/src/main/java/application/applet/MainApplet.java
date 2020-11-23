package application.applet;

import application.dataType.Cell;
import com.fazecast.jSerialComm.SerialPort;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import processing.core.PApplet;

import java.io.InputStream;
import java.util.HashMap;

public class MainApplet extends PApplet {

    PApplet processing;

    SerialPort comPort;

    Table<Integer, Integer, Cell> mazeData = HashBasedTable.create();

    int rowCurrentIndex = 0;
    int columnCurrentIndex = 0;
    int headingCurrent = 0;

    @Override
    public void settings() {
        size(500, 500, P3D);
    }

    @Override
    public void setup() {
        processing = this;

        mazeData.put(0, 0, new Cell(0, 0));

        Cell a = new Cell(1, 0);
        a.visit();
        mazeData.put(1, 0, a);

        println(mazeData);
        println(mazeData.get(0, 0).isSurveyComplete());
        println(mazeData.get(1, 0).isSurveyComplete());

        printArray(SerialPort.getCommPorts());

        HashMap<String, String> buffer = new HashMap<>();

        buffer.put("a", "dfsdfsdfsf");
        println(buffer.get("a"));

        comPort = SerialPort.getCommPort("COM6");
        comPort.openPort();
        comPort.setBaudRate(115200);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        x();
    }

    void x() {
        HashMap<String, String> buffer = new HashMap<>();

        try {
            InputStream stream = comPort.getInputStream();
            comPort.clearBreak();
            char charTemp;
            StringBuilder stringTemp = new StringBuilder();

            for (int j = 0; j < 5000; ++ j) {
                charTemp = ((char) stream.read());

                if (charTemp == '[') {
                    stringTemp.setLength(0);
                    stringTemp.append(charTemp);
                } else if (charTemp == ']') {
                    stringTemp.append(charTemp);

                    try{
                        String topic = match(stringTemp.toString(),"(?<=\\[).+?(?=\\:)")[0];
                        String content = match(stringTemp.toString(),"(?<=\\:).+?(?=\\])")[0];

                        System.out.println("topic: " + topic + "\t  content: " + content);

                        buffer.put(topic,content);
                    } catch (Exception ignored) {
                    }
                } else {
                    stringTemp.append(charTemp);
                }
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        println(buffer);
    }

    @Override
    public void draw() {
        background(255, 0, 0);
    }
}
