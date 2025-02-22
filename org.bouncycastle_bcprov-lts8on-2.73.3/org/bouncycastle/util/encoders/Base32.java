/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base32Encoder;
import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Encoder;
import org.bouncycastle.util.encoders.EncoderException;

public class Base32 {
    private static final Encoder encoder = new Base32Encoder();

    public static String toBase32String(byte[] data) {
        return Base32.toBase32String(data, 0, data.length);
    }

    public static String toBase32String(byte[] data, int off, int length) {
        byte[] encoded = Base32.encode(data, off, length);
        return Strings.fromByteArray(encoded);
    }

    public static byte[] encode(byte[] data) {
        return Base32.encode(data, 0, data.length);
    }

    public static byte[] encode(byte[] data, int off, int length) {
        int len = encoder.getEncodedLength(length);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
        try {
            encoder.encode(data, off, length, bOut);
        }
        catch (Exception e) {
            throw new EncoderException("exception encoding base32 string: " + e.getMessage(), e);
        }
        return bOut.toByteArray();
    }

    public static int encode(byte[] data, OutputStream out) throws IOException {
        return encoder.encode(data, 0, data.length, out);
    }

    public static int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        return encoder.encode(data, off, length, out);
    }

    public static byte[] decode(byte[] data) {
        int len = data.length / 8 * 5;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
        try {
            encoder.decode(data, 0, data.length, bOut);
        }
        catch (Exception e) {
            throw new DecoderException("unable to decode base32 data: " + e.getMessage(), e);
        }
        return bOut.toByteArray();
    }

    public static byte[] decode(String data) {
        int len = data.length() / 8 * 5;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
        try {
            encoder.decode(data, bOut);
        }
        catch (Exception e) {
            throw new DecoderException("unable to decode base32 string: " + e.getMessage(), e);
        }
        return bOut.toByteArray();
    }

    public static int decode(String data, OutputStream out) throws IOException {
        return encoder.decode(data, out);
    }

    public static int decode(byte[] base32Data, int start, int length, OutputStream out) {
        try {
            return encoder.decode(base32Data, start, length, out);
        }
        catch (Exception e) {
            throw new DecoderException("unable to decode base32 data: " + e.getMessage(), e);
        }
    }
}

