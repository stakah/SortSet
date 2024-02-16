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
  import java.io.InputStream;
  import java.io.OutputStream;
  import java.net.Socket;
  import java.net.SocketAddress;

          /**
    * A multi-threaded unix socket server that simply echoes all input, byte per byte.
    *
    * @author Christian Kohlschütter
    */
          public final class EchoServer extends DemoServerBase {
    public EchoServer(SocketAddress listenAddress) {
              super(listenAddress);
            }

            @Override
    protected void doServeSocket(Socket socket) throws IOException {
              int bufferSize = socket.getReceiveBufferSize();
              byte[] buffer = new byte[bufferSize];

              try (InputStream is = socket.getInputStream(); //
          OutputStream os = socket.getOutputStream()) {
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                          os.write(buffer, 0, read);
                        }
                  }
            }
  }
