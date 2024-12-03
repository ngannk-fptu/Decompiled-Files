/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

public final class Base64 {
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 63;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int FOURBYTE = 4;
    private static final byte PAD = 61;
    private static byte[] base64Alphabet;
    private static byte[] lookUpBase64Alphabet;
    static final int[] base64;

    static boolean isBase64(byte octect) {
        return octect == 61 || base64Alphabet[octect] != -1;
    }

    static boolean isArrayByteBase64(byte[] arrayOctect) {
        int length = arrayOctect.length;
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (Base64.isBase64(arrayOctect[i])) continue;
            return false;
        }
        return true;
    }

    public static byte[] encode(byte[] binaryData) {
        int lengthDataBits = binaryData.length * 8;
        int fewerThan24bits = lengthDataBits % 24;
        int numberTriplets = lengthDataBits / 24;
        byte[] encodedData = null;
        encodedData = fewerThan24bits != 0 ? new byte[(numberTriplets + 1) * 4] : new byte[numberTriplets * 4];
        byte k = 0;
        byte l = 0;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        for (i = 0; i < numberTriplets; ++i) {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];
            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 3);
            encodedIndex = i * 4;
            encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[b2 >> 4 | k << 4];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2 | b3 >> 6];
            encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3F];
        }
        dataIndex = i * 3;
        encodedIndex = i * 4;
        if (fewerThan24bits == 8) {
            b1 = binaryData[dataIndex];
            k = (byte)(b1 & 3);
            encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 2] = 61;
            encodedData[encodedIndex + 3] = 61;
        } else if (fewerThan24bits == 16) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 3);
            encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[b2 >> 4 | k << 4];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = 61;
        }
        return encodedData;
    }

    public byte[] decode(byte[] base64Data) {
        int numberQuadruple = base64Data.length / 4;
        byte[] decodedData = null;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        byte b4 = 0;
        byte marker0 = 0;
        byte marker1 = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        decodedData = new byte[numberQuadruple * 3 + 1];
        for (int i = 0; i < numberQuadruple; ++i) {
            dataIndex = i * 4;
            marker0 = base64Data[dataIndex + 2];
            marker1 = base64Data[dataIndex + 3];
            b1 = base64Alphabet[base64Data[dataIndex]];
            b2 = base64Alphabet[base64Data[dataIndex + 1]];
            if (marker0 != 61 && marker1 != 61) {
                b3 = base64Alphabet[marker0];
                b4 = base64Alphabet[marker1];
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
                decodedData[encodedIndex + 2] = (byte)(b3 << 6 | b4);
            } else if (marker0 == 61) {
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4);
                decodedData[encodedIndex + 2] = 0;
            } else if (marker1 == 61) {
                b3 = base64Alphabet[marker0];
                decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
                decodedData[encodedIndex + 2] = (byte)(b3 << 6);
            }
            encodedIndex += 3;
        }
        return decodedData;
    }

    public static String base64Decode(String orig) {
        char[] chars = orig.toCharArray();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int shift = 0;
        int acc = 0;
        for (i = 0; i < chars.length; ++i) {
            int v = base64[chars[i] & 0xFF];
            if (v >= 64) {
                if (chars[i] == '=') continue;
                System.out.println("Wrong char in base64: " + chars[i]);
                continue;
            }
            acc = acc << 6 | v;
            if ((shift += 6) < 8) continue;
            sb.append((char)(acc >> (shift -= 8) & 0xFF));
        }
        return sb.toString();
    }

    static {
        int i;
        base64Alphabet = new byte[255];
        lookUpBase64Alphabet = new byte[63];
        for (i = 0; i < 255; ++i) {
            Base64.base64Alphabet[i] = -1;
        }
        for (i = 90; i >= 65; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 65);
        }
        for (i = 122; i >= 97; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 97 + 26);
        }
        for (i = 57; i >= 48; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 48 + 52);
        }
        Base64.base64Alphabet[43] = 62;
        Base64.base64Alphabet[47] = 63;
        for (i = 0; i <= 25; ++i) {
            Base64.lookUpBase64Alphabet[i] = (byte)(65 + i);
        }
        i = 26;
        int j = 0;
        while (i <= 51) {
            Base64.lookUpBase64Alphabet[i] = (byte)(97 + j);
            ++i;
            ++j;
        }
        i = 52;
        j = 0;
        while (i <= 61) {
            Base64.lookUpBase64Alphabet[i] = (byte)(48 + j);
            ++i;
            ++j;
        }
        base64 = new int[]{64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64};
    }
}

