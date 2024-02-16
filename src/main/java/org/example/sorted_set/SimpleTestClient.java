package org.example.sorted_set;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;
import java.net.SocketException;

public class SimpleTestClient {

    public static void main(String[] args) throws IOException {
        final File socketFile = new File("./socket");
        boolean connected = false;
        try (AFUNIXSocket sock = AFUNIXSocket.connectTo(AFUNIXSocketAddress.of(socketFile));
             InputStream is = sock.getInputStream();
             OutputStream os = sock.getOutputStream();
             DataInputStream din = new DataInputStream(is);
             DataOutputStream dout = new DataOutputStream(os);
        ) {
            System.out.println("Client - Connected");
            connected = true;
            byte[] buf = new byte[128];

            String message = "Hello from Client";
            System.out.println("Sending message: " + message);
            os.write(message.getBytes("UTF-8"));

            int read = is.read(buf);
            System.out.println("Server says: " + new String(buf, 0, read, "UTF-8"));

            System.out.println("Replying to server...");
            os.write("Hello Server".getBytes("UTF-8"));
            os.flush();

        } catch (SocketException e) {
            if (!connected) {
                System.out.println("Cannot connect to server. Have you started it?");
                System.out.println();
            }
            throw e;
        }

        System.out.println("End of communication.");
    }
}
