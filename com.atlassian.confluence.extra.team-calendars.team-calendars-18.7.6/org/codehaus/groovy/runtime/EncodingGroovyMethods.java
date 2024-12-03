/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.StringWriterIOException;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.codehaus.groovy.runtime.EncodingGroovyMethodsSupport;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class EncodingGroovyMethods {
    private static final char[] T_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static final String CHUNK_SEPARATOR = "\r\n";

    public static Writable encodeBase64(Byte[] data, boolean chunked) {
        return EncodingGroovyMethods.encodeBase64(DefaultTypeTransformation.convertToByteArray(data), chunked);
    }

    public static Writable encodeBase64(Byte[] data) {
        return EncodingGroovyMethods.encodeBase64(DefaultTypeTransformation.convertToByteArray(data), false);
    }

    public static Writable encodeBase64(final byte[] data, final boolean chunked) {
        return new Writable(){

            @Override
            public Writer writeTo(Writer writer) throws IOException {
                int charCount = 0;
                int dLimit = data.length / 3 * 3;
                for (int dIndex = 0; dIndex != dLimit; dIndex += 3) {
                    int d = (data[dIndex] & 0xFF) << 16 | (data[dIndex + 1] & 0xFF) << 8 | data[dIndex + 2] & 0xFF;
                    writer.write(T_TABLE[d >> 18]);
                    writer.write(T_TABLE[d >> 12 & 0x3F]);
                    writer.write(T_TABLE[d >> 6 & 0x3F]);
                    writer.write(T_TABLE[d & 0x3F]);
                    if (!chunked || ++charCount != 19) continue;
                    writer.write(EncodingGroovyMethods.CHUNK_SEPARATOR);
                    charCount = 0;
                }
                if (dLimit != data.length) {
                    int d = (data[dLimit] & 0xFF) << 16;
                    if (dLimit + 1 != data.length) {
                        d |= (data[dLimit + 1] & 0xFF) << 8;
                    }
                    writer.write(T_TABLE[d >> 18]);
                    writer.write(T_TABLE[d >> 12 & 0x3F]);
                    writer.write(dLimit + 1 < data.length ? T_TABLE[d >> 6 & 0x3F] : 61);
                    writer.write(61);
                    if (chunked && charCount != 0) {
                        writer.write(EncodingGroovyMethods.CHUNK_SEPARATOR);
                    }
                }
                return writer;
            }

            public String toString() {
                StringWriter buffer = new StringWriter();
                try {
                    this.writeTo(buffer);
                }
                catch (IOException e) {
                    throw new StringWriterIOException(e);
                }
                return buffer.toString();
            }
        };
    }

    public static Writable encodeBase64(byte[] data) {
        return EncodingGroovyMethods.encodeBase64(data, false);
    }

    public static byte[] decodeBase64(String value) {
        int byteShift = 4;
        int tmp = 0;
        boolean done = false;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i != value.length(); ++i) {
            int sixBit;
            char c = value.charAt(i);
            int n = sixBit = c < '{' ? EncodingGroovyMethodsSupport.TRANSLATE_TABLE[c] : 66;
            if (sixBit < 64) {
                if (done) {
                    throw new RuntimeException("= character not at end of base64 value");
                }
                tmp = tmp << 6 | sixBit;
                if (byteShift-- != 4) {
                    buffer.append((char)(tmp >> byteShift * 2 & 0xFF));
                }
            } else if (sixBit == 64) {
                --byteShift;
                done = true;
            } else if (sixBit == 66) {
                throw new RuntimeException("bad character in base64 value");
            }
            if (byteShift != 0) continue;
            byteShift = 4;
        }
        try {
            return buffer.toString().getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Base 64 decode produced byte values > 255");
        }
    }

    public static Writable encodeHex(Byte[] data) {
        return EncodingGroovyMethods.encodeHex(DefaultTypeTransformation.convertToByteArray(data));
    }

    public static Writable encodeHex(final byte[] data) {
        return new Writable(){

            @Override
            public Writer writeTo(Writer out) throws IOException {
                for (int i = 0; i < data.length; ++i) {
                    String hexString = Integer.toHexString(data[i] & 0xFF);
                    if (hexString.length() < 2) {
                        out.write("0");
                    }
                    out.write(hexString);
                }
                return out;
            }

            public String toString() {
                StringWriter buffer = new StringWriter();
                try {
                    this.writeTo(buffer);
                }
                catch (IOException e) {
                    throw new StringWriterIOException(e);
                }
                return buffer.toString();
            }
        };
    }

    public static byte[] decodeHex(String value) {
        if (value.length() % 2 != 0) {
            throw new NumberFormatException("odd number of characters in hex string");
        }
        byte[] bytes = new byte[value.length() / 2];
        for (int i = 0; i < value.length(); i += 2) {
            bytes[i / 2] = (byte)Integer.parseInt(value.substring(i, i + 2), 16);
        }
        return bytes;
    }
}

