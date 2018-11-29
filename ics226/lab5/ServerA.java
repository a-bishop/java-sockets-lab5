package ics226.lab5;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class ServerA {

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        int backlog = 1;

        ServerSocket socket = new ServerSocket(port, backlog);
        while (true) {
            Socket client = socket.accept();
            BufferedInputStream bin = new BufferedInputStream(client.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(client.getOutputStream());

            String ready = "READY\n";
            byte[] bufferOut = ready.getBytes(Charset.forName("UTF-8"));
            bout.write(bufferOut);
            bout.flush();

            byte[] bufferIn = new byte[1024];
            int size = bin.read(bufferIn);
            //System.out.println("size " + size);

            char operatorChar = '`';

            //unpack data
            byte operator = bufferIn[0];

            // get the operator
            if ((operator & 0b1) != 0) operatorChar = '+';
            else if ((operator & 0b10) != 0) operatorChar = '-';
            else if ((operator & 0b100) != 0) operatorChar = '*';

            boolean start = true;

            int runningCount = (int) bufferIn[1];
            int first;
            int second;
            byte currByte;
            int result = 0;

            for (int i=2; i<size; i++) {
                if (runningCount == 0) break;
                currByte = bufferIn[i];
                first = (currByte >>> 4) & 0x0F;
                second = currByte & 0x0F;
                if (start) {
                    switch (operatorChar) {
                        case '+':
                            result = first + second;
                            break;
                        case '-':
                            result = first - second;
                            break;
                        case '*':
                            result = first * second;
                    }
                    start = false;
                    runningCount -= 2;
                } else if (runningCount == 1) {
                    switch (operatorChar) {
                        case '+':
                            result = result + first;
                            break;
                        case '-':
                            result = result - first;
                            break;
                        case '*':
                            result = result * first;
                            break;
                    }
                    runningCount -= 1;
                } else {
                    switch (operatorChar) {
                        case '+':
                            result = result + first + second;
                            break;
                        case '-':
                            result = result - first - second;
                            break;
                        case '*':
                            result = result * first * second;
                            break;
                    }
                    runningCount -= 2;
                }
            }

            //System.out.println("result " + result);

            byte[] return_packet = new byte[4];

            byte byte1 = (byte) ((result >> 24) & 0x0FF);
            return_packet[0] = byte1;

            byte byte2 = (byte) ((result >> 16) & 0x0FF);
            return_packet[1] = byte2;

            byte byte3 = (byte) ((result >> 8) & 0x0FF);
            return_packet[2] = byte3;

            byte byte4 = (byte) (result & 0x0FF);
            return_packet[3] = byte4;

//            System.out.println("b1 " + byte1);
//            System.out.println("b2 " + byte2);
//            System.out.println("b3 " + byte3);
//            System.out.println("b4 " + byte4);

            bout.write(return_packet);
            bout.flush();

        }
    }
}
