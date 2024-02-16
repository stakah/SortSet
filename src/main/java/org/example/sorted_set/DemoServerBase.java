package org.example.sorted_set;

/*
2    * junixsocket
3    *
4    * Copyright 2009-2023 Christian Kohlschütter
5    *
6    * Licensed under the Apache License, Version 2.0 (the "License");
7    * you may not use this file except in compliance with the License.
8    * You may obtain a copy of the License at
9    *
10   *     http://www.apache.org/licenses/LICENSE-2.0
11   *
12   * Unless required by applicable law or agreed to in writing, software
13   * distributed under the License is distributed on an "AS IS" BASIS,
14   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
15   * See the License for the specific language governing permissions and
16   * limitations under the License.
17   */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Future;

import org.newsclub.net.unix.AFSocketAddress;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.server.SocketServer;

/**
 32   * An {@link SocketServer} that's just good for demo purposes.
 33   *
 34   * @author Christian Kohlschütter
 35   */
@SuppressWarnings("CatchAndPrintStackTrace" /* errorprone */)
abstract class DemoServerBase extends SocketServer<SocketAddress, Socket, ServerSocket> {
    public DemoServerBase(SocketAddress listenAddress) {
              super(listenAddress);
            }

            private static String millisToHumanReadable(int millis, String zeroValue) {
              if (millis == 0 && zeroValue != null) {
                    return "0 [ms] (" + zeroValue + ")";
                  } else {
                    float secs = millis / 1000f;
                    if ((secs - (int) secs) == 0) {
                          return millis + " [ms] == " + (int) (secs) + "s";
                        } else {
                          return millis + " [ms] == " + secs + "s";
                        }
                  }
            }

            @Override
    protected void onServerStarting() {
              System.out.println();
              System.out.println("Creating server: " + getClass().getName());
              System.out.println("with the following configuration:");
              System.out.println("- maxConcurrentConnections: " + getMaxConcurrentConnections());
              System.out.println("- serverTimeout: " + millisToHumanReadable(getServerTimeout(), "none"));
              System.out.println("- socketTimeout: " + millisToHumanReadable(getSocketTimeout(), "none"));
              System.out.println("- serverBusyTimeout: " + millisToHumanReadable(getServerBusyTimeout(),
                          "none"));
            }

            @Override
    protected void onServerBound(SocketAddress address) {
              System.out.println("Created server -- bound to " + address);
            }

            @Override
    protected void onServerBusy(long busySince) {
              System.out.println("Server is busy");
            }

            @Override
    protected void onServerReady(int activeCount) {
              System.out.println("Active connections: " + activeCount
                          + "; waiting for the next connection...");
            }

            @Override
    protected void onServerStopped(ServerSocket theServerSocket) {
              System.out.println("Close server " + theServerSocket);
            }

            @Override
    protected void onSubmitted(Socket socket, Future<?> submit) {
              System.out.println("Accepted: " + socket);
            }

            @Override
    protected void onBeforeServingSocket(Socket socket) {
              System.out.println("Serving socket: " + socket);
              if (socket instanceof AFUNIXSocket) {
                    try {
                          System.out.println("Client's credentials: " + ((AFUNIXSocket) socket).getPeerCredentials());
                        } catch (IOException e) {
                         e.printStackTrace();
                       }
                 }
           }

           @Override
   protected void onServerShuttingDown() {
             System.out.println("Nothing going on for a long time, I better stop listening");
           }

           @Override
   protected void onSocketExceptionDuringAccept(SocketException e) {
             e.printStackTrace();
           }

           @Override
   protected void onSocketExceptionAfterAccept(Socket socket, SocketException e) {
             System.out.println("Closed (not executed): " + socket);
           }

           @Override
   protected void onServingException(Socket socket, Throwable t) {
             if (socket.isClosed()) {
                   // "Broken pipe", etc.
                   System.out.println("The other end disconnected (" + t.getMessage() + "): " + socket);
                   return;
                 }
             System.err.println("Exception thrown in " + socket + ", connected: " + socket.isConnected()
                         + ", " + socket.isBound() + "," + socket.isClosed() + "," + socket.isInputShutdown() + ","
                         + socket.isOutputShutdown());
             t.printStackTrace();
           }

           @Override
   protected void onAfterServingSocket(Socket socket) {
             System.out.println("Closed: " + socket);
           }

           @Override
   protected void onListenException(Exception e) {
             e.printStackTrace();
           }

           @Override
   protected ServerSocket newServerSocket() throws IOException {
             SocketAddress listenAddress = getListenAddress();
             if (listenAddress instanceof AFSocketAddress) {
                  return ((AFSocketAddress) listenAddress).getAddressFamily().newServerSocket();
                 } else {
                   return new ServerSocket();
                 }
           }
 }