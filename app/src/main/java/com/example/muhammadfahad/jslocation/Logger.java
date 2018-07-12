package com.example.muhammadfahad.jslocation;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Logger {
    public static void logcat()
    {

        try

        {
//            Process proc = Runtime.getRuntime().exec("logcat -c");
//            new InputStreamReader(proc.getInputStream());
            Process process = Runtime.getRuntime().exec("logcat -d");
            InputStreamReader in=new InputStreamReader(process.getInputStream());
//            udpClient(in);
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                log.append(line);
                log.append("\n");
            }

            //Convert log to string
            final String logString = new String(log.toString());

            //Create txt file in SD Card
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() +File.separator + "Log File location");

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            File file = new File(dir, "logcat.txt");

            //To write logcat in text file
            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            //Writing the string to file

            osw.write(logString);

            osw.flush();
            osw.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void udpClient(final InputStreamReader text) {
        try{
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("53.53.53.29");
            byte[] sendData = new byte[1024];
//        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader inFromUser = new BufferedReader(text);
            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}