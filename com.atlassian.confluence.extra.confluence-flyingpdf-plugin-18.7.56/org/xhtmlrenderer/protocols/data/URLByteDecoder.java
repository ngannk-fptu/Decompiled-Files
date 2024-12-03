/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.protocols.data;

class URLByteDecoder {
    URLByteDecoder() {
    }

    public static byte[] decode(String s) {
        byte[] buffer = new byte[s.length()];
        int bindex = 0;
        block4: for (int index = 0; index < s.length(); ++index) {
            char c = s.charAt(index);
            switch (c) {
                case '+': {
                    buffer[bindex++] = 32;
                    continue block4;
                }
                case '%': {
                    buffer[bindex++] = (byte)Integer.parseInt(s.substring(index + 1, index + 3), 16);
                    index += 2;
                    continue block4;
                }
                default: {
                    buffer[bindex++] = (byte)c;
                }
            }
        }
        byte[] result = new byte[bindex];
        System.arraycopy(buffer, 0, result, 0, bindex);
        return result;
    }
}

