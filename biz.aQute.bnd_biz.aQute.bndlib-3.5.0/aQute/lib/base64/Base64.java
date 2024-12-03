/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.regex.Pattern;

public class Base64 {
    private static final int DEFAULT_MAX_INPUT_LENGTH = 65000;
    byte[] data;
    static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    static byte[] values;
    static Pattern BASE64_P;

    public Base64(byte[] data) {
        this.data = data;
    }

    public static final byte[] decodeBase64(String string) {
        try {
            return Base64.decodeBase64(new StringReader(string));
        }
        catch (IOException e) {
            return null;
        }
    }

    public static byte[] decodeBase64(Reader rdr) throws IOException {
        return Base64.decodeBase64(rdr, 65000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decodeBase64(Reader rdr, int maxLength) throws IOException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(maxLength);
            Base64.decode(rdr, bout, maxLength);
            byte[] byArray = bout.toByteArray();
            return byArray;
        }
        finally {
            rdr.close();
        }
    }

    public static byte[] decodeBase64(InputStream in) throws IOException {
        return Base64.decodeBase64(in, 65000);
    }

    public static byte[] decodeBase64(InputStream in, int maxLength) throws IOException {
        return Base64.decodeBase64(new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII)), maxLength);
    }

    public static final byte[] decodeBase64(File file) throws IOException {
        if (file.length() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File " + file + " is >4Gb for base 64 decoding");
        }
        try {
            return Base64.decodeBase64(Files.newBufferedReader(file.toPath(), StandardCharsets.US_ASCII), (int)file.length() * 2 / 3);
        }
        catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException(iae.getMessage() + ": " + file, iae);
        }
    }

    public static final void decode(Reader rdr, OutputStream out) throws IOException {
        Base64.decode(rdr, out, 65000);
    }

    public static final void decode(Reader rdr, OutputStream out, int maxLength) throws IOException {
        int c;
        int register = 0;
        int i = 0;
        int pads = 0;
        byte[] test = new byte[3];
        while ((c = rdr.read()) >= 0) {
            if (--maxLength < 0) {
                throw new IllegalArgumentException("Input stream for base64 decoding is too large");
            }
            if (Character.isWhitespace(c) || c == 13 || c == 10) continue;
            if (c > 127) {
                throw new IllegalArgumentException("Invalid base64 character in " + rdr + ", character value > 128 ");
            }
            byte v = 0;
            if (c == 61) {
                ++pads;
            } else {
                v = values[c];
                if (v < 0) {
                    throw new IllegalArgumentException("Invalid base64 character in " + rdr + ", " + c);
                }
            }
            register <<= 6;
            test[2] = (byte)((register |= v) & 0xFF);
            test[1] = (byte)(register >> 8 & 0xFF);
            test[0] = (byte)(register >> 16 & 0xFF);
            if (++i % 4 != 0) continue;
            Base64.flush(out, register, pads);
            register = 0;
            pads = 0;
        }
    }

    private static void flush(OutputStream out, int register, int pads) throws IOException {
        switch (pads) {
            case 0: {
                out.write(0xFF & register >> 16);
                out.write(0xFF & register >> 8);
                out.write(0xFF & register >> 0);
                break;
            }
            case 1: {
                out.write(0xFF & register >> 16);
                out.write(0xFF & register >> 8);
                break;
            }
            case 2: {
                out.write(0xFF & register >> 16);
            }
        }
    }

    public Base64(String s) {
        this.data = Base64.decodeBase64(s);
    }

    public String toString() {
        return Base64.encodeBase64(this.data);
    }

    public static String encodeBase64(InputStream in) throws IOException {
        StringWriter sw = new StringWriter();
        Base64.encode(in, (Appendable)sw);
        return sw.toString();
    }

    public static String encodeBase64(File in) throws IOException {
        StringWriter sw = new StringWriter();
        Base64.encode(in, (Appendable)sw);
        return sw.toString();
    }

    public static String encodeBase64(byte[] data) {
        StringWriter sw = new StringWriter();
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        try {
            Base64.encode(bin, (Appendable)sw);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return sw.toString();
    }

    public Object toData() {
        return this.data;
    }

    public static void encode(File in, Appendable sb) throws IOException {
        if (in.length() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File > 4Gb " + in);
        }
        Base64.encode(new BufferedInputStream(Files.newInputStream(in.toPath(), new OpenOption[0])), sb, (int)in.length());
    }

    public static void encode(InputStream in, Appendable sb) throws IOException {
        Base64.encode(in, sb, 65000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void encode(InputStream in, Appendable sb, int maxLength) throws IOException {
        try {
            int mod;
            int buf = 0;
            int bits = 0;
            int out = 0;
            while (true) {
                if (bits >= 6) {
                    int v = 0x3F & buf >> (bits -= 6);
                    sb.append(alphabet.charAt(v));
                    ++out;
                    continue;
                }
                int c = in.read();
                if (c < 0) break;
                if (--maxLength < 0) {
                    throw new IllegalArgumentException("Length (" + maxLength + ") for base 64 encode exceeded");
                }
                buf <<= 8;
                buf |= 0xFF & c;
                bits += 8;
            }
            if (bits != 0) {
                sb.append(alphabet.charAt(0x3F & buf << 6 - bits));
                ++out;
            }
            if ((mod = 4 - out % 4) != 4) {
                for (int i = 0; i < mod; ++i) {
                    sb.append('=');
                }
            }
        }
        finally {
            in.close();
        }
    }

    public static boolean isBase64(String value) {
        return BASE64_P.matcher(value).matches();
    }

    static {
        int i;
        values = new byte[128];
        BASE64_P = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?");
        for (i = 0; i < values.length; ++i) {
            Base64.values[i] = -1;
        }
        for (i = 0; i < alphabet.length(); ++i) {
            char c = alphabet.charAt(i);
            Base64.values[c] = (byte)i;
        }
    }
}

