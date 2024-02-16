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
import java.net.Socket;
import java.net.SocketAddress;

import org.newsclub.net.unix.AFSocketAddress;
import org.newsclub.net.unix.AFUNIXSocket;

/**
 * An {@link AFUNIXSocket} client that's just good for demo purposes.
 *
 * @author Christian Kohlschütter
 */
abstract class DemoClientBase {
    private Socket socket;

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public void connect(SocketAddress endpoint) throws IOException {
        System.out.println("Connect " + this + " to " + endpoint);
        if (endpoint instanceof AFSocketAddress) {
            socket = ((AFSocketAddress) endpoint).getAddressFamily().newSocket();
        } else {
            socket = new Socket();
        }
        socket.connect(endpoint);

        handleSocket(socket);
    }

    @SuppressWarnings("hiding")
    protected abstract void handleSocket(Socket socket) throws IOException;
}
