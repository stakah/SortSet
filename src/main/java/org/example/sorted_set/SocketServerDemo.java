package org.example.sorted_set;

import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.net.SocketAddress;
import java.net.SocketException;

public class SocketServerDemo {
    public static void main(String[] args) throws SocketException {
        final EchoServer echoServer;

        String socketName = "./socket";
        final SocketAddress listenAddress = AFUNIXSocketAddress.of(new File(socketName));
        System.out.println("Listen address: " + listenAddress);

        echoServer = new EchoServer(listenAddress);

        echoServer.setMaxConcurrentConnections(8);
        echoServer.setServerTimeout(0);
        echoServer.setSocketTimeout(60000);
        echoServer.setServerBusyTimeout(1000);

        echoServer.start();
    }
}
