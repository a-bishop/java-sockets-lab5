package ics226.lab5;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class ClientB {
    public static void main(String[] args) throws IOException {

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        char operator = args[2].charAt(0);
        int num_count = args.length - 3;
        int byteArrayLength = 7;

        if (num_count <= 1) {
            System.out.println("please enter two or more integers");
            System.exit(0);
        }

        if (num_count > 10) {
            System.out.println("Please only enter up to ten integers between zero and 15");
            System.exit(0);
        }

        int operatorNum;

        switch (operator) {
            case '+':
                operatorNum = 1;
                break;
            case '-':
                operatorNum = 2;
                break;
            case '*':
                operatorNum = 4;
                break;
            default:
                operatorNum = 0;
                System.out.println("please enter an operator (+, -, *) to process the numbers");
                System.exit(0);
        }

        byte[] bufferOut = new byte[byteArrayLength];
        bufferOut[0] = (byte) operatorNum;
        bufferOut[1] = (byte) num_count;

        int num1;
        int num2;
        int nums;
        int j = 3;
        int currArg;
        int nextArg;
        int nextByte;
        int potentialLastVal = args.length-1;
        boolean oddNumberOfArgs = (num_count % 2 ==1);

        for (int i=2; i<num_count+1; i++) {
            currArg = j;
            nextArg = j+1;
            nextByte = i;
            num1 = (Integer.parseInt(args[currArg]) << 4) & 0x0FF;
            if (nextArg >= potentialLastVal) {
                if (oddNumberOfArgs) {
                    nums = num1;
                    bufferOut[nextByte] = (byte) nums;
                    break;
                } else {
                    num2 = (Integer.parseInt(args[nextArg])) & 0x0F;
                    nums = (num1 | num2) & 0x0FF;
                    bufferOut[nextByte] = (byte) nums;
                    break;
                }
            } else {
                num2 = (Integer.parseInt(args[nextArg])) & 0x0F;
                nums = (num1 | num2) & 0x0FF;
                bufferOut[nextByte] = (byte) nums;
                j+=2;
            }
        }

        Socket socket = new Socket(host, port);

        BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());

        BufferedReader bre = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line = bre.readLine();
        //System.out.println(line);

        if (line.equals("READY")) {
            bout.write(bufferOut);
            bout.flush();
        }

        DataInputStream din = new DataInputStream(bin);

        int total = din.readInt();

        System.out.println(total);
        socket.close();

    }
}
