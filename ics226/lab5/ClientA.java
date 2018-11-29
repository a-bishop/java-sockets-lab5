package ics226.lab5;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class ClientA {
    public static void main(String[] args) throws IOException {

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        char operator = args[2].charAt(0);
        int num_count = args.length - 3;
        int byteArrayLength = 7;
//        System.out.println(host);
//        System.out.println(port);
//        System.out.println(operator);
//        System.out.println(num_count);
//        System.out.println(byteArrayLength);

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

        //for (int num : bufferOut) System.out.println(num);

        Socket socket = new Socket(host, port);
        BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());

        byte[] bufferIn = new byte[6];
        bin.read(bufferIn);

        String data = new String(bufferIn, Charset.forName("UTF-8"));
//        System.out.println("data (readymsg)" + data);

        if (data.equals("READY\n")) {
            bout.write(bufferOut);
            bout.flush();
//            System.out.println("sending packet...");
        }

        bufferIn = new byte[4];
        bin.read(bufferIn);

        int data1 = (bufferIn[0] << 24) & 0xFF000000;
        int data2 = (bufferIn[1] << 16) & 0xFF0000;
        int data3 = (bufferIn[2] << 8) & 0xFF00;
        int data4 = bufferIn[3] & 0x0FF;
        int total = data1 + data2 + data3 + data4;

//        System.out.println("buff1 " + bufferIn[0]);
//        System.out.println("buff2 " + bufferIn[1]);
//        System.out.println("buff3 " + bufferIn[2]);
//        System.out.println("buff4 " + bufferIn[3]);
//
//        System.out.println("d1 " + data1);
//        System.out.println("d2 " + data2);
//        System.out.println("d3 " + data3);
//        System.out.println("d4 " + data4);


        System.out.println(total);
        socket.close();

    }
}
