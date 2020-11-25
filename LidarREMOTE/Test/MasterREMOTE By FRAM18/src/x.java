package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.PrintWriter;
import java.util.Scanner;
public class Test {
    static boolean received;
    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPort("COM5");
        port.setComPortParameters(9600,8,1,0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER,0,0);
        System.out.println("Open port: " + port.openPort());
        Scanner in = new Scanner(port.getInputStream());
        PrintWriter out = new PrintWriter(port.getOutputStream(),true);
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                String input = "";

               input = in.nextLine();

                System.out.println("return: " + input);
                received=true;
            }
        });


int counter =0;
        while(!received) {
            System.out.println(counter);
            out.println(counter);
            out.flush();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        out.println('w');
        System.out.println("w");
           /*     String input = in.nextLine();
                System.out.println("return: "+input+input.isEmpty());*/
    }
}