package ics226.lab5;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerB {

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        int backlog = 5;

        ServerSocket socket = new ServerSocket(port, backlog);
        while (true) {
            Socket client = socket.accept();
            BufferedInputStream bin = new BufferedInputStream(client.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(client.getOutputStream());

            PrintWriter pwr = new PrintWriter(client.getOutputStream());

            pwr.println("READY");
            pwr.flush();

            byte[] bufferIn = new byte[1024];
            int size = bin.read(bufferIn);

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

            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeInt(result);
            dout.flush();
        }
    }
}

