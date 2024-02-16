package org.example.sorted_set;

import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;

public class RankingServerDemo {
    public static void main(String[] args) throws IOException {
        final RankingServer rankingServer;

        String socketName = "./socket";
        final SocketAddress listenAddress = AFUNIXSocketAddress.of(new File(socketName));
        System.out.println("Listen address: " + listenAddress);

        rankingServer = new RankingServer(listenAddress);

        rankingServer.setMaxConcurrentConnections(2);
        rankingServer.setServerTimeout(0);
        rankingServer.setSocketTimeout(60000);
        rankingServer.setServerBusyTimeout(1000);

        rankingServer.start();
    }
}
