package org.example.sorted_set;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.sorted_set.Util.*;

public class SimpleRankingClient {
    static List<List<Integer>> queries = new ArrayList<>();
    static int qix = 0;
    public static void init() {
        queries.add(Arrays.asList(4, 1, 10, 5, 346248660));
        queries.add(Arrays.asList(4, 1,  2, 5, 346248661));
        queries.add(Arrays.asList(4, 1,  2, 8, 0x83828180));
        queries.add(Arrays.asList(3, 4,  2, 8));
        queries.add(Arrays.asList(5, 5, 10, 0, 122145695, 1413614583));
        queries.add(Arrays.asList(3, 4, 10, 5));
        queries.add(Arrays.asList(4, 1, 5, 8, 1427404471));
        queries.add(Arrays.asList(4, 1, 2, 1, 1761359496));
        queries.add(Arrays.asList(4, 1, 2, 4, 631790567));
        queries.add(Arrays.asList(3, 4, 2, 4));
        queries.add(Arrays.asList(3, 4, 2, 1));
        queries.add(Arrays.asList(4, 1, 7, 3, 1989599602));
        queries.add(Arrays.asList(2, 3, 2));
        queries.add(Arrays.asList(3, 2, 2, 1));
        queries.add(Arrays.asList(2, 3, 2));
        queries.add(Arrays.asList(9, 5, 2, 3, 5, 7, 10, 0, 113558404, 1516732979));
        queries.add(Arrays.asList(1, 6));

    }

    private synchronized static int nextQ() {
        if (qix == queries.size()) return -1;
        return qix++;
    }
    private static AFUNIXSocket connect() throws IOException {
        final File socketFile = new File("./socket");
        boolean connected = false;
        AFUNIXSocket sock = AFUNIXSocket.connectTo(AFUNIXSocketAddress.of(socketFile));
        return sock;
    }

    private static void client(String name) throws IOException {
        boolean connected = false;
        try (AFUNIXSocket sock = connect();
             InputStream is = sock.getInputStream();
             OutputStream os = sock.getOutputStream();
             DataInputStream din = new DataInputStream(is);
             DataOutputStream dout = new DataOutputStream(os);
        ) {
            System.out.println(String.format("Client '%s' - Connected", name));
            connected = true;
            byte[] buf = new byte[128];
            for (int qx=nextQ(); qx != -1; qx=nextQ()) {
                List<Integer> q = queries.get(qx);
                String message = q.stream().map(v -> String.valueOf(v))
                        .collect(Collectors.joining(" "));
                byte arr[] = convetToByteArray(q);
                System.out.println(String.format("Client '%s' - Sending token: %s\n%s",
                        name, message, Util.toString(arr)));
                os.write(arr);
                sleep(10);
                os.flush();

                if (q.get(1) == 6) break;
                System.out.println(String.format("Client '%s' - Waiting for server response ...", name));

                int read, timeoutCounter = 100;
                while ((read = is.read(buf)) == -1) {
                    System.out.print("#");
                    timeoutCounter--;
                    if (timeoutCounter == 0) break;
                    sleep(100);
                }
                System.out.println("!");

                if (read != -1) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buf, 0, read);
                    int[] tokens = extractTokens(buf);
                    System.out.println(String.format("Client '%s' - << %s\n%s",
                            name, Arrays.toString(tokens), Util.toString(tokens)));
                } else {
                    System.out.println(String.format("Client '%s' - Response timed out", name));
                }
            }

        } catch (SocketException e) {
            if (!connected) {
                System.out.println(String.format("Client '%s' - Cannot connect to server. Have you started it?", name));
                System.out.println();
            }
            throw e;
        }
        System.out.println(String.format("Client '%s' - End of communication.", name));
    }

    public static void main(String[] args) throws IOException {
        init();

        for (int client=1; client<=8; client++) {
            String finalClient = String.valueOf(client);
            new Thread(() -> {
                try {
                    client(finalClient);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException ex) {};
    }



}


