package org.example.sorted_set;

import org.newsclub.net.unix.AFUNIXServerSocket;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ForkJoinPool;

public abstract class SimpleRankingServer {
    private SocketAddress listenAddress;
    Thread listenThread = null;
    ForkJoinPool connectionPool;
    public int maxConcurrentConnections = 8;
    public int socketTimeout = 600000;

    private ServerSocket serverSocket;

    private ServerSocket newServerSocket() {
        return serverSocket;
    }
    public SimpleRankingServer(SocketAddress listenAddress) {
        this.listenAddress = listenAddress;
    }

    public boolean isRunning() {
        synchronized (this) {
            return (listenThread != null && listenThread.isAlive());
        }
    }
    public void start() {
        synchronized (this) {
            if (isRunning()) {
                return;
            }
        }

        if (connectionPool == null) {
            connectionPool = new ForkJoinPool(maxConcurrentConnections,
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
        }
        final File socketFile = new File(new File("./socket"),
                "junixsocket-test.sock");
        System.out.println(socketFile);

        try(AFUNIXServerSocket server = AFUNIXServerSocket.newInstance();) {
            server.bind(listenAddress);
            serverSocket = server;
            System.out.println("server: " + server);

            Thread t = new Thread(SimpleRankingServer.this.toString() + " listening thread") {
                @Override
                public void run() {
                    try {
                        listen();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } catch (Throwable e) {
                        throw e;
                    }
                }
            };

            t.start();
            listenThread = t;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() throws IOException {
        ServerSocket server = null;

        try {
            if (server == null) {
                server = newServerSocket();
            }
            if (!server.isBound()) {
                server.bind(listenAddress);
            }
            acceptLoop(server);
        } finally {
            server.close();
        }
    }
    private void acceptLoop(ServerSocket server) throws IOException {
        boolean remoteReady = false;
        acceptLoop : while (!Thread.interrupted()) {
            try {
                System.out.println("Waiting for connection...");
                if (server == null) {
                    break;
                }

                final Socket socket;
                try {
                    Socket theSocket = (Socket) server.accept();
                    socket = theSocket;
                } catch (SocketException e) {
                    if (server.isClosed()) {
                        break acceptLoop;
                    } else {
                        throw e;
                    }
                }

                try {
                    socket.setSoTimeout(socketTimeout);
                } catch (SocketException e) {
                    socket.close();

                    continue acceptLoop;
                }

                connectionPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doServeSocket(socket);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } catch (Throwable e) {
                            throw e;
                        }

                        try {
                            socket.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                });
            } catch (SocketTimeoutException e) {
                if (!connectionPool.isQuiescent()) {
                    continue acceptLoop;
                } else {
                    connectionPool.shutdown();
                    break acceptLoop;
                }
            }
        }
    }

    protected abstract void doServeSocket(Socket socket) throws IOException;
}
