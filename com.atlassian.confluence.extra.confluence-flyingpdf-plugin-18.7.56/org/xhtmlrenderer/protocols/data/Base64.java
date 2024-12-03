/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.protocols.data;

class Base64 {
    private static String _map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    Base64() {
    }

    public static byte[] decode(String s) {
        if (s == null || s.length() < 4) {
            return new byte[0];
        }
        if ((s = s.replaceAll("[^A-Za-z0-9\\+\\/\\=]+", "")).length() < 4) {
            return new byte[0];
        }
        int padding = 0;
        if (s.charAt(s.length() - 1) == '=') {
            ++padding;
        }
        if (s.charAt(s.length() - 2) == '=') {
            ++padding;
        }
        byte[] input = s.getBytes();
        byte[] output = new byte[s.length() / 4 * 3 - padding];
        int outputIndex = 0;
        for (int i = 0; i < input.length; i += 4) {
            int e = _map.indexOf(input[i]) << 18 | _map.indexOf(input[i + 1]) << 12 | _map.indexOf(input[i + 2]) << 6 | _map.indexOf(input[i + 3]);
            output[outputIndex++] = (byte)(e >> 16 & 0xFF);
            if (input[i + 2] == 61) continue;
            output[outputIndex++] = (byte)(e >> 8 & 0xFF);
            if (input[i + 3] == 61) continue;
            output[outputIndex++] = (byte)(e & 0xFF);
        }
        return output;
    }
}

