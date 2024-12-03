/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Base64 {
    private static final String CHARSET = "US-ASCII";
    private static final char[] BASE64CHARS;
    private static final byte[] DECODETABLE;
    private static final char BASE64PAD = '=';

    private Base64() {
    }

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(Base64.decodeOrEncode(arg));
        }
    }

    public static String decodeOrEncode(String data) {
        if (data.startsWith("{base64}")) {
            return Base64.decode(data.substring("{base64}".length()));
        }
        return "{base64}" + Base64.encode(data);
    }

    public static String decodeIfEncoded(String data) {
        if (data != null && data.startsWith("{base64}")) {
            return Base64.decode(data.substring("{base64}".length()));
        }
        return data;
    }

    public static long calcEncodedLength(long dataLength) {
        long encLen = dataLength * 4L / 3L;
        encLen += (encLen + 4L) % 4L;
        return encLen;
    }

    public static long guessDecodedLength(long encLength) {
        long decLen = encLength * 3L / 4L;
        return decLen + 3L;
    }

    public static void encode(InputStream in, Writer writer) throws IOException {
        int read;
        byte[] buffer = new byte[9216];
        while ((read = in.read(buffer)) > 0) {
            Base64.encode(buffer, 0, read, writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void encode(InputStream in, OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
        try {
            Base64.encode(in, writer);
        }
        finally {
            try {
                ((Writer)writer).flush();
            }
            catch (IOException iOException) {}
        }
    }

    public static void encode(byte[] data, int off, int len, Writer writer) throws IOException {
        int i;
        if (len == 0) {
            return;
        }
        if (len < 0 || off >= data.length || len + off > data.length) {
            throw new IllegalArgumentException();
        }
        char[] enc = new char[4];
        while (len >= 3) {
            i = ((data[off] & 0xFF) << 16) + ((data[off + 1] & 0xFF) << 8) + (data[off + 2] & 0xFF);
            enc[0] = BASE64CHARS[i >> 18];
            enc[1] = BASE64CHARS[i >> 12 & 0x3F];
            enc[2] = BASE64CHARS[i >> 6 & 0x3F];
            enc[3] = BASE64CHARS[i & 0x3F];
            writer.write(enc, 0, 4);
            off += 3;
            len -= 3;
        }
        if (len == 1) {
            i = data[off] & 0xFF;
            enc[0] = BASE64CHARS[i >> 2];
            enc[1] = BASE64CHARS[i << 4 & 0x3F];
            enc[2] = 61;
            enc[3] = 61;
            writer.write(enc, 0, 4);
        } else if (len == 2) {
            i = ((data[off] & 0xFF) << 8) + (data[off + 1] & 0xFF);
            enc[0] = BASE64CHARS[i >> 10];
            enc[1] = BASE64CHARS[i >> 4 & 0x3F];
            enc[2] = BASE64CHARS[i << 2 & 0x3F];
            enc[3] = 61;
            writer.write(enc, 0, 4);
        }
    }

    public static String encode(String data) {
        try {
            StringWriter buffer = new StringWriter();
            byte[] b = data.getBytes(StandardCharsets.UTF_8);
            Base64.encode(b, 0, b.length, buffer);
            return buffer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to encode base64 data: " + data, e);
        }
    }

    public static String decode(String data) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Base64.decode(data, (OutputStream)buffer);
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e) {
            return data;
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to decode base64 data: " + data, e);
        }
    }

    public static void decode(Reader reader, OutputStream out) throws IOException {
        int read;
        char[] chunk = new char[8192];
        while ((read = reader.read(chunk)) > -1) {
            Base64.decode(chunk, 0, read, out);
        }
    }

    public static void decode(InputStream in, OutputStream out) throws IOException {
        Base64.decode(new InputStreamReader(in, CHARSET), out);
    }

    public static void decode(String data, OutputStream out) throws IOException {
        char[] chars = data.toCharArray();
        Base64.decode(chars, 0, chars.length, out);
    }

    public static void decode(char[] chars, OutputStream out) throws IOException {
        Base64.decode(chars, 0, chars.length, out);
    }

    public static void decode(char[] chars, int off, int len, OutputStream out) throws IOException {
        if (len == 0) {
            return;
        }
        if (len < 0 || off >= chars.length || len + off > chars.length) {
            throw new IllegalArgumentException();
        }
        char[] chunk = new char[4];
        byte[] dec = new byte[3];
        int posChunk = 0;
        for (int i = off; i < off + len; ++i) {
            char c = chars[i];
            if (c < DECODETABLE.length && DECODETABLE[c] != 127 || c == '=') {
                chunk[posChunk++] = c;
                if (posChunk != chunk.length) continue;
                byte b0 = DECODETABLE[chunk[0]];
                byte b1 = DECODETABLE[chunk[1]];
                byte b2 = DECODETABLE[chunk[2]];
                byte b3 = DECODETABLE[chunk[3]];
                if (chunk[3] == '=' && chunk[2] == '=') {
                    dec[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                    out.write(dec, 0, 1);
                } else if (chunk[3] == '=') {
                    dec[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                    dec[1] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                    out.write(dec, 0, 2);
                } else {
                    dec[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                    dec[1] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                    dec[2] = (byte)(b2 << 6 & 0xC0 | b3 & 0x3F);
                    out.write(dec, 0, 3);
                }
                posChunk = 0;
                continue;
            }
            if (Character.isWhitespace(c)) continue;
            throw new IllegalArgumentException("specified data is not base64 encoded");
        }
    }

    static {
        int i;
        BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        DECODETABLE = new byte[128];
        for (i = 0; i < DECODETABLE.length; ++i) {
            Base64.DECODETABLE[i] = 127;
        }
        for (i = 0; i < BASE64CHARS.length; ++i) {
            Base64.DECODETABLE[Base64.BASE64CHARS[i]] = (byte)i;
        }
    }
}

