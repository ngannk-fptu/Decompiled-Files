/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Hex {
    private static final Log LOG = LogFactory.getLog(Hex.class);
    private static final byte[] HEX_BYTES = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private Hex() {
    }

    public static String getString(byte b) {
        char[] chars = new char[]{HEX_CHARS[Hex.getHighNibble(b)], HEX_CHARS[Hex.getLowNibble(b)]};
        return new String(chars);
    }

    public static String getString(byte[] bytes) {
        StringBuilder string = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            string.append(HEX_CHARS[Hex.getHighNibble(b)]).append(HEX_CHARS[Hex.getLowNibble(b)]);
        }
        return string.toString();
    }

    public static byte[] getBytes(byte b) {
        return new byte[]{HEX_BYTES[Hex.getHighNibble(b)], HEX_BYTES[Hex.getLowNibble(b)]};
    }

    public static byte[] getBytes(byte[] bytes) {
        byte[] asciiBytes = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; ++i) {
            asciiBytes[i * 2] = HEX_BYTES[Hex.getHighNibble(bytes[i])];
            asciiBytes[i * 2 + 1] = HEX_BYTES[Hex.getLowNibble(bytes[i])];
        }
        return asciiBytes;
    }

    public static char[] getChars(short num) {
        char[] hex = new char[]{HEX_CHARS[num >> 12 & 0xF], HEX_CHARS[num >> 8 & 0xF], HEX_CHARS[num >> 4 & 0xF], HEX_CHARS[num & 0xF]};
        return hex;
    }

    public static char[] getCharsUTF16BE(String text) {
        char[] hex = new char[text.length() * 4];
        int charIdx = 0;
        for (int stringIdx = 0; stringIdx < text.length(); ++stringIdx) {
            char c = text.charAt(stringIdx);
            hex[charIdx++] = HEX_CHARS[c >> 12 & 0xF];
            hex[charIdx++] = HEX_CHARS[c >> 8 & 0xF];
            hex[charIdx++] = HEX_CHARS[c >> 4 & 0xF];
            hex[charIdx++] = HEX_CHARS[c & 0xF];
        }
        return hex;
    }

    public static void writeHexByte(byte b, OutputStream output) throws IOException {
        output.write(HEX_BYTES[Hex.getHighNibble(b)]);
        output.write(HEX_BYTES[Hex.getLowNibble(b)]);
    }

    public static void writeHexBytes(byte[] bytes, OutputStream output) throws IOException {
        for (byte b : bytes) {
            Hex.writeHexByte(b, output);
        }
    }

    private static int getHighNibble(byte b) {
        return (b & 0xF0) >> 4;
    }

    private static int getLowNibble(byte b) {
        return b & 0xF;
    }

    public static byte[] decodeBase64(String base64Value) {
        try {
            Class<?> b64Class = Class.forName("java.util.Base64");
            Method getDecoderMethod = b64Class.getMethod("getDecoder", new Class[0]);
            Object base64Decoder = getDecoderMethod.invoke(b64Class, new Object[0]);
            Method decodeMethod = base64Decoder.getClass().getMethod("decode", String.class);
            return (byte[])decodeMethod.invoke(base64Decoder, base64Value.replaceAll("\\s", ""));
        }
        catch (ClassNotFoundException ex) {
            LOG.debug((Object)ex);
        }
        catch (IllegalAccessException ex) {
            LOG.debug((Object)ex);
        }
        catch (IllegalArgumentException ex) {
            LOG.debug((Object)ex);
        }
        catch (NoSuchMethodException ex) {
            LOG.debug((Object)ex);
        }
        catch (SecurityException ex) {
            LOG.debug((Object)ex);
        }
        catch (InvocationTargetException ex) {
            LOG.debug((Object)ex);
        }
        try {
            Class<?> datatypeConverterClass = Class.forName("javax.xml.bind.DatatypeConverter");
            Method parseBase64BinaryMethod = datatypeConverterClass.getMethod("parseBase64Binary", String.class);
            return (byte[])parseBase64BinaryMethod.invoke(null, base64Value);
        }
        catch (ClassNotFoundException ex) {
            LOG.debug((Object)ex);
        }
        catch (IllegalAccessException ex) {
            LOG.debug((Object)ex);
        }
        catch (IllegalArgumentException ex) {
            LOG.debug((Object)ex);
        }
        catch (NoSuchMethodException ex) {
            LOG.debug((Object)ex);
        }
        catch (SecurityException ex) {
            LOG.debug((Object)ex);
        }
        catch (InvocationTargetException ex) {
            LOG.debug((Object)ex);
        }
        LOG.error((Object)"Can't decode base64 value, try adding javax.xml.bind:jaxb-api to your build");
        return new byte[0];
    }

    public static byte[] decodeHex(String s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        while (i < s.length() - 1) {
            if (s.charAt(i) == '\n' || s.charAt(i) == '\r') {
                ++i;
                continue;
            }
            String hexByte = s.substring(i, i + 2);
            try {
                baos.write(Integer.parseInt(hexByte, 16));
            }
            catch (NumberFormatException ex) {
                LOG.error((Object)("Can't parse " + hexByte + ", aborting decode"), (Throwable)ex);
                break;
            }
            i += 2;
        }
        return baos.toByteArray();
    }
}

