package application.service;

import com.fazecast.jSerialComm.SerialPort;
import processing.core.PApplet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SerialEventBus extends Thread {
    HashMap<String, String> hashMap;
    SerialPort port;

    Scanner scanner;
    String inputBuffer;

    PrintWriter output;
    StringBuilder outputStringBuilder = new StringBuilder();

    public SerialEventBus(String portName) {
        port = SerialPort.getCommPort(portName);

        init();
    }

    public SerialEventBus(String portName, int baudRate) {
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(baudRate);

        init();
    }

    private void init() {
        hashMap = new HashMap<>();

        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        scanner = new Scanner(port.getInputStream());
        output = new PrintWriter(port.getOutputStream(), true);

        this.open();
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            inputBuffer = "";

            try {
                inputBuffer = scanner.nextLine();

                int first = inputBuffer.indexOf('[');
                int middle = inputBuffer.indexOf(':');
                int last = inputBuffer.indexOf(']');

                if (first < middle && middle < last) {
                    String topic = PApplet.match(inputBuffer, "(?<=\\[).+?(?=\\:)")[0];
                    String content = PApplet.match(inputBuffer, "(?<=\\:).+?(?=\\])")[0];

                    hashMap.put(topic, content);

                    System.out.println(" \ttopic : " + topic + "\tcontent : " + content);
                }
            } catch (Exception ignore) {
            }
        }
    }

    private void open() {
        port.openPort();
    }

    public void send(String topic, String content) {
        outputStringBuilder.setLength(0);
        outputStringBuilder.append('[')
                .append(topic)
                .append(':')
                .append(content).append(']');

        output.println(outputStringBuilder);
        hashMap.put(topic, content);
    }

    public String read(String topic) {
        return hashMap.getOrDefault(topic, "");
    }

    public String readNonContain(String topic) {
        String buffer = read(topic);
        hashMap.put(topic, "");
        send(topic, hashMap.get(topic));
        return buffer;
    }

    public Map<String, String> showAll() {
        return hashMap;
    }
}
