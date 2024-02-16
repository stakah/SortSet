package org.example.sorted_set;

import java.io.File;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;



/*
Java by default does not support UNIX Domain Sockets. Use junixsocket 3rd party library instead.
Documentaion: http://goo.gl/kveo6z
*/

public class Solution {

    // NOTE: Use this path to create the UDS Server socket
    static String SERVER_SOCKET_PATH = "./socket";

    public static void main(String[] args) {
        System.out.println("Solution hello");
        testcase();

        int v = 0x83828180;
        byte[] b = Util.toBytes(v);

        System.out.println(String.format("%6d: %08x ", v, v));
        for (int i=0; i<b.length; i++)
            System.out.print(String.format("%02x ", b[i]));
        System.out.println();

        v = Util.extractInt(b, 0);
        System.out.println(String.format("%6d: %08x ", v, v));

    }

    public static void testcase() {
        /*
        4 1 10 5 346248660                    | addScore(set:10, key:5, score:346248660)
        5 5 10 0 122145695 1413614583         | getScoreInRange(sets:Arrays.asList(10), lower:122145695, upper:1413614583)
        3 4 10 5                              | getScore(set:10, key:5)
        4 1 5 8 1427404471                    | addScore(set:5, key:8, score:1427404471)
        4 1 2 1 1761359496                    | addScore(set:2, key:1, score:1761359496)
        4 1 2 4 631790567                     | addScore(set:2, key:4, score:631790567)
        3 4 2 4                               | getScore(set:2, key:4)
        3 4 2 1                               | getScore(set:2, key:1)
        4 1 7 3 1989599602                    | addScore(set:7, key:3, score:1989599602)
        2 3 2                                 | getSize(set:2)
        3 2 2 1                               | removeKey(set:2, key:1)
        2 3 2                                 | getSize(set:2)
        9 5 2 3 5 7 10 0 113558404 1516732979 | getScoreInRange(sets:Arrays.asList(2, 3, 5, 7, 10), lower:113558404, upper:1516732979)
        1 6
        */
        RankingSystem rankingSystem = new RankingSystem();

        System.out.println("4 1 10 5 346248660                    | addScore(set:10, key:5, score:346248660)");
        System.out.println(rankingSystem.addScore(10, 5, 346248660));

        System.out.println("4 1 2 5 346248661                    | addScore(set:10, key:5, score:346248660)");
        System.out.println(rankingSystem.addScore(2, 5, 346248661));

        System.out.println("5 5 10 0 122145695 1413614583         | getScoreInRange(sets:Arrays.asList(10), lower:122145695, upper:1413614583)");
        List<Score> scores = rankingSystem.getScoreInRange(Arrays.asList(10), 122145695, 1413614583);
        Collections.sort(scores, new ScoreComparator());

        System.out.println(scores.size() * 2 + " ");
        for (Score score : scores) {
            System.out.println(String.format("key:%d", score.key));
            System.out.println(String.format("score:%d", score.score));
        }

        System.out.println("3 4 10 5                              | getScore(set:10, key:5)");
        System.out.println(rankingSystem.getScore(10, 5));

        System.out.println("4 1 5 8 1427404471                    | addScore(set:5, key:8, score:1427404471)");
        System.out.println(rankingSystem.addScore(5, 8, 1427404471));

        System.out.println("4 1 2 1 1761359496                    | addScore(set:2, key:1, score:1761359496)");
        System.out.println(rankingSystem.addScore(2, 1, 1761359496));

        System.out.println("4 1 2 4 631790567                     | addScore(set:2, key:4, score:631790567)");
        System.out.println(rankingSystem.addScore(2, 4, 631790567));

        System.out.println("3 4 2 4                               | getScore(set:2, key:4)");
        System.out.println(rankingSystem.getScore(2, 4));

        System.out.println("3 4 2 1                               | getScore(set:2, key:1)");
        System.out.println(rankingSystem.getScore(2, 1));

        System.out.println("4 1 7 3 1989599602                    | addScore(set:7, key:3, score:1989599602)");
        System.out.println(rankingSystem.addScore(7, 3, 1989599602));

        System.out.println("2 3 2                                 | getSize(set:2)");
        System.out.println(rankingSystem.getSize(2));

        System.out.println("3 2 2 1                               | removeKey(set:2, key:1)");
        System.out.println(rankingSystem.removeKey(2, 1));

        System.out.println("2 3 2                                 | getSize(set:2)");
        System.out.println(rankingSystem.getSize(2));

        System.out.println("9 5 2 3 5 7 10 0 113558404 1516732979 | getScoreInRange(sets:Arrays.asList(2, 3, 5, 7, 10), lower:113558404, upper:1516732979)");
        scores = rankingSystem.getScoreInRange(Arrays.asList(2, 3, 5, 7, 10), 113558404, 1516732979);
        Collections.sort(scores, new ScoreComparator());
        System.out.println(scores.size() * 2 + " ");
        for (Score score : scores) {
            System.out.println(String.format("key:%d", score.key));
            System.out.println(String.format("score:%d", score.score));
        }

        System.out.println("1 6                                   | DISCONNECT");
    }
}

