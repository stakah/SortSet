package org.example.sorted_set;

import java.util.List;

public class Util {
    public static int extractInt(byte[] bytes, int start) {
        int result = ((int) (bytes[start  ] << 24))
                   + ((int) (bytes[start+1] << 16) & 0x00ff0000)
                   + ((int) (bytes[start+2] <<  8) & 0x0000ff00)
                   | ((int) (bytes[start+3]      ) & 0x000000ff);
        return result;
    }
    public static byte[] convetToByteArray(List<Integer> intArray) {
        byte byteArray[] = new byte[intArray.size() * 4];
        int j = 0;
        for (Integer v : intArray) {
            byte[] aux = toBytes(v);
            for (int k=0; k<4; k++) {
                byteArray[j+k] = aux[k];
            }
            j += 4;
        }

        return byteArray;
    }

    public static byte[] toBytes(int v) {
        byte[] result = new byte[4];
        result[0] = (byte) (v >> 24);
        result[1] = (byte) (v >> 16);
        result[2] = (byte) (v >> 8);
        result[3] = (byte) (v /*>> 0*/);

        return result;
    }

    public static String toString(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<arr.length-1; i++) {
            sb.append(String.format("%02x, ", arr[i]));
        }
        sb.append(String.format("%02x", arr[arr.length-1]));
        sb.append("]");

        return sb.toString();
    }

    public static String toString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (arr.length > 0) {
            byte[] b = toBytes(arr[0]);
            sb.append(String.format("%02x %02x %02x %02x", b[0], b[1], b[2], b[3]));
            for (int i = 1; i < arr.length; i++) {
                b = toBytes(arr[i]);
                sb.append(String.format(", %02x %02x %02x %02x", b[0], b[1], b[2], b[3]));
            }
        }
        sb.append("]");

        return sb.toString();
    }

    public static int[] extractTokens(byte[] bytes) {
        int nTokens = extractInt(bytes, 0);
        int[] result = new int[nTokens+1];
        result[0] = nTokens;
        for (int i=1; i<=nTokens; i++) {
            result[i] = extractInt(bytes, 4*i);
        }

        return result;
    }

}
