package application.service;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SerialService {
    SerialPort comPort;
    InputStream stream;

    HashMap<String, String> buffer = new HashMap<>();

    public SerialService(String portName) {
        comPort = SerialPort.getCommPort(portName);

        init();
    }

    public SerialService(String portName, int baudRate) {
        comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(baudRate);

        init();
    }

    private void init(){
        stream = comPort.getInputStream();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
    }

    public SerialService open() {
        comPort.openPort();
        return this;
    }

    public SerialService close() {
        comPort.closePort();
        return this;
    }

    public int readInt(String topic) {
        try {
            System.out.print((char) stream.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 5;
    }
}
