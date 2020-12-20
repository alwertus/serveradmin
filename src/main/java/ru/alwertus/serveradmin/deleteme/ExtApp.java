package ru.alwertus.serveradmin.deleteme;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ExtApp {
    @Getter
    private String name;

    private String status;

    @Getter
    private int port;

    public ExtApp(String name, int port) {
        this.name = name;
//        status = "WORKING";
        this.port = port;
    }

    public String getStatus() {
        return available(port) ? "TRUE" : "FALSE";
    }

    public static boolean available(int port) {
        int MIN_PORT_NUMBER = 0;
        int MAX_PORT_NUMBER = 65535;
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static void telnet(int port) {
        try {
            Socket pingSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                pingSocket = new Socket("192.168.1.8", port);
                out = new PrintWriter(pingSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
            } catch (IOException e) {
                return;
            }

            out.println("ping");
            System.out.println(in.readLine());
            out.close();
            in.close();
            pingSocket.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }
}


/*
public static void telnet(int port) {
        try {
            Socket pingSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                pingSocket = new Socket("192.168.1.8", port);
                out = new PrintWriter(pingSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
            } catch (IOException e) {
                return;
            }

            out.println("ping");
            System.out.println(in.readLine());
            out.close();
            in.close();
            pingSocket.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }
* */