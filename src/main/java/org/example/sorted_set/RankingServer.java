package org.example.sorted_set;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.sorted_set.Util.*;

public class RankingServer extends DemoServerBase {
//public class RankingServer extends SimpleRankingServer {
    RankingSystem rankingSystem;
    public RankingServer (SocketAddress listenAddress) {
        super(listenAddress);
        rankingSystem = new RankingSystem();
    }

        @Override
        protected void doServeSocket(Socket socket) throws IOException {
            int bufferSize = socket.getReceiveBufferSize();
            byte[] buffer = new byte[bufferSize];

            try (InputStream is = socket.getInputStream(); //
                 OutputStream os = socket.getOutputStream()) {
                int read;
                int nTokens = 0;
                int tokens[] = new int[0];
                int i = 0;
                while ((read = is.read(buffer)) != -1) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                    tokens = extractTokens(byteBuffer.array());

                    List<Integer> response = new ArrayList<>();
                    if (byteBuffer.get(7) == 1) {
                        System.out.println("cmd:5 Bytes :" + Arrays.toString(byteBuffer.array()));
                    }
                    System.out.println("Received: " + Arrays.toString(tokens));
                    nTokens = tokens[0];

                    boolean disconnect = performCmd(nTokens, tokens, response);
                    if (disconnect == true) {
                        break;
                    }

                    System.out.println("Sending: " + response);
                    byte[] responseBytes = convetToByteArray(response);
                    os.write(responseBytes);

                    System.out.println("Cycling ...");
                }
            }
        }

        private boolean performCmd(int nTokens, int[] tokens, List<Integer> response) {
            int cmd = tokens[1];
            boolean disconnect = false;

            System.out.println("cmd: " + cmd);
            int responseSize = 0;
            int set, key, score;
            switch (cmd) {
                case 1: // addScore
                    set = tokens[2]; key = tokens[3]; score=tokens[4];
                    rankingSystem.addScore(set, key, score);
                    response.add(0);
                    break;
                case 2: // removeKey
                    set = tokens[2]; key = tokens[3];
                    rankingSystem.removeKey(set, key);
                    response.add(0);
                    break;
                case 3: // getSize
                    set = tokens[2];
                    int size = rankingSystem.getSize(set);
                    responseSize = 1;
                    response.add(responseSize);
                    response.add( size);
                    break;
                case 4: // getScore
                    set = tokens[2]; key = tokens[3];
                    score = rankingSystem.getScore(set, key);
                    responseSize = 1;
                    response.add(responseSize);
                    response.add(score);
                    break;
                case 5: // getScoreInRange
                    List<Integer> setList = new ArrayList<>();
                    int lower = 0, upper = 0;
                    for (int i=2; i<=nTokens; i++) {
                        if (tokens[i] == 0) {
                            lower = tokens[i+1];
                            upper = tokens[i+2];
                            break;
                        }
                        setList.add(tokens[i]);
                    }

                    System.out.println(String.format("lower: %d upper:%d", lower, upper));
                    System.out.println("setList:" + setList);
                    List<Score> scores = rankingSystem.getScoreInRange(setList, lower, upper);
                    Collections.sort(scores, new ScoreComparator());

                    responseSize = scores.size() * 2;
                    response.add(responseSize);
                    for (Score sc : scores) {
                        response.add(sc.key);
                        response.add(sc.score);
                    }
                    break;
                case 6: // DISCONNECT
                    disconnect = true;
                    break;
                default: // Unknown command
                    break;
            }

            return disconnect;
        }





}
