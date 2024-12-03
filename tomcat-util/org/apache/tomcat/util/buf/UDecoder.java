/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

public final class UDecoder {
    private static final StringManager sm = StringManager.getManager(UDecoder.class);
    @Deprecated
    public static final boolean ALLOW_ENCODED_SLASH = Boolean.parseBoolean(System.getProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "false"));
    private static final IOException EXCEPTION_EOF = new DecodeException(sm.getString("uDecoder.eof"));
    private static final IOException EXCEPTION_NOT_HEX_DIGIT = new DecodeException(sm.getString("uDecoder.isHexDigit"));
    private static final IOException EXCEPTION_SLASH = new DecodeException(sm.getString("uDecoder.noSlash"));

    public void convert(ByteChunk mb, boolean query) throws IOException {
        if (query) {
            this.convert(mb, true, EncodedSolidusHandling.DECODE);
        } else {
            this.convert(mb, false, EncodedSolidusHandling.REJECT);
        }
    }

    public void convert(ByteChunk mb, EncodedSolidusHandling encodedSolidusHandling) throws IOException {
        this.convert(mb, false, encodedSolidusHandling);
    }

    private void convert(ByteChunk mb, boolean query, EncodedSolidusHandling encodedSolidusHandling) throws IOException {
        int start = mb.getOffset();
        byte[] buff = mb.getBytes();
        int end = mb.getEnd();
        int idx = ByteChunk.findByte(buff, start, end, (byte)37);
        int idx2 = -1;
        if (query) {
            idx2 = ByteChunk.findByte(buff, start, idx >= 0 ? idx : end, (byte)43);
        }
        if (idx < 0 && idx2 < 0) {
            return;
        }
        if (idx2 >= 0 && idx2 < idx || idx < 0) {
            idx = idx2;
        }
        int j = idx;
        while (j < end) {
            if (buff[j] == 43 && query) {
                buff[idx] = 32;
            } else if (buff[j] != 37) {
                buff[idx] = buff[j];
            } else {
                if (j + 2 >= end) {
                    throw EXCEPTION_EOF;
                }
                byte b1 = buff[j + 1];
                byte b2 = buff[j + 2];
                if (!UDecoder.isHexDigit(b1) || !UDecoder.isHexDigit(b2)) {
                    throw EXCEPTION_NOT_HEX_DIGIT;
                }
                j += 2;
                int res = UDecoder.x2c(b1, b2);
                if (res == 47) {
                    switch (encodedSolidusHandling) {
                        case DECODE: {
                            buff[idx] = (byte)res;
                            break;
                        }
                        case REJECT: {
                            throw EXCEPTION_SLASH;
                        }
                        case PASS_THROUGH: {
                            buff[idx++] = buff[j - 2];
                            buff[idx++] = buff[j - 1];
                            buff[idx] = buff[j];
                        }
                    }
                } else {
                    buff[idx] = (byte)res;
                }
            }
            ++j;
            ++idx;
        }
        mb.setEnd(idx);
    }

    @Deprecated
    public void convert(CharChunk mb, boolean query) throws IOException {
        int start = mb.getOffset();
        char[] buff = mb.getBuffer();
        int cend = mb.getEnd();
        int idx = CharChunk.indexOf(buff, start, cend, '%');
        int idx2 = -1;
        if (query) {
            idx2 = CharChunk.indexOf(buff, start, idx >= 0 ? idx : cend, '+');
        }
        if (idx < 0 && idx2 < 0) {
            return;
        }
        if (idx2 >= 0 && idx2 < idx || idx < 0) {
            idx = idx2;
        }
        boolean noSlash = !ALLOW_ENCODED_SLASH && !query;
        int j = idx;
        while (j < cend) {
            if (buff[j] == '+' && query) {
                buff[idx] = 32;
            } else if (buff[j] != '%') {
                buff[idx] = buff[j];
            } else {
                if (j + 2 >= cend) {
                    throw EXCEPTION_EOF;
                }
                char b1 = buff[j + 1];
                char b2 = buff[j + 2];
                if (!UDecoder.isHexDigit(b1) || !UDecoder.isHexDigit(b2)) {
                    throw EXCEPTION_NOT_HEX_DIGIT;
                }
                j += 2;
                int res = UDecoder.x2c(b1, b2);
                if (noSlash && res == 47) {
                    throw EXCEPTION_SLASH;
                }
                buff[idx] = (char)res;
            }
            ++j;
            ++idx;
        }
        mb.setEnd(idx);
    }

    @Deprecated
    public void convert(MessageBytes mb, boolean query) throws IOException {
        switch (mb.getType()) {
            case 1: {
                String strValue = mb.toString();
                if (strValue == null) {
                    return;
                }
                try {
                    mb.setString(this.convert(strValue, query));
                    break;
                }
                catch (RuntimeException ex) {
                    throw new DecodeException(ex.getMessage());
                }
            }
            case 3: {
                CharChunk charC = mb.getCharChunk();
                this.convert(charC, query);
                break;
            }
            case 2: {
                ByteChunk bytesC = mb.getByteChunk();
                this.convert(bytesC, query);
            }
        }
    }

    @Deprecated
    public String convert(String str, boolean query) {
        if (str == null) {
            return null;
        }
        if (!(query && str.indexOf(43) >= 0 || str.indexOf(37) >= 0)) {
            return str;
        }
        boolean noSlash = !ALLOW_ENCODED_SLASH && !query;
        StringBuilder dec = new StringBuilder();
        int strPos = 0;
        int strLen = str.length();
        dec.ensureCapacity(str.length());
        while (strPos < strLen) {
            char laChar;
            int laPos;
            for (laPos = strPos; !(laPos >= strLen || (laChar = str.charAt(laPos)) == '+' && query || laChar == '%'); ++laPos) {
            }
            if (laPos > strPos) {
                dec.append(str.substring(strPos, laPos));
                strPos = laPos;
            }
            if (strPos >= strLen) break;
            char metaChar = str.charAt(strPos);
            if (metaChar == '+') {
                dec.append(' ');
                ++strPos;
                continue;
            }
            if (metaChar != '%') continue;
            char res = (char)Integer.parseInt(str.substring(strPos + 1, strPos + 3), 16);
            if (noSlash && res == '/') {
                throw new IllegalArgumentException(sm.getString("uDecoder.noSlash"));
            }
            dec.append(res);
            strPos += 3;
        }
        return dec.toString();
    }

    @Deprecated
    public static String URLDecode(String str) {
        return UDecoder.URLDecode(str, StandardCharsets.UTF_8);
    }

    public static String URLDecode(String str, Charset charset) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(37) == -1) {
            return str;
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() * 2);
        OutputStreamWriter osw = new OutputStreamWriter((OutputStream)baos, charset);
        char[] sourceChars = str.toCharArray();
        int len = sourceChars.length;
        int ix = 0;
        try {
            while (ix < len) {
                char c;
                if ((c = sourceChars[ix++]) == '%') {
                    osw.flush();
                    if (ix + 2 > len) {
                        throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.missingDigit", str));
                    }
                    char c1 = sourceChars[ix++];
                    char c2 = sourceChars[ix++];
                    if (UDecoder.isHexDigit(c1) && UDecoder.isHexDigit(c2)) {
                        baos.write(UDecoder.x2c(c1, c2));
                        continue;
                    }
                    throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.missingDigit", str));
                }
                osw.append(c);
            }
            osw.flush();
            return baos.toString(charset.name());
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.conversionError", str, charset.name()), ioe);
        }
    }

    private static boolean isHexDigit(int c) {
        return c >= 48 && c <= 57 || c >= 97 && c <= 102 || c >= 65 && c <= 70;
    }

    private static int x2c(byte b1, byte b2) {
        int digit = b1 >= 65 ? (b1 & 0xDF) - 65 + 10 : b1 - 48;
        digit *= 16;
        return digit += b2 >= 65 ? (b2 & 0xDF) - 65 + 10 : b2 - 48;
    }

    private static int x2c(char b1, char b2) {
        int digit = b1 >= 'A' ? (b1 & 0xDF) - 65 + 10 : b1 - 48;
        digit *= 16;
        return digit += b2 >= 'A' ? (b2 & 0xDF) - 65 + 10 : b2 - 48;
    }

    private static class DecodeException
    extends CharConversionException {
        private static final long serialVersionUID = 1L;

        DecodeException(String s) {
            super(s);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

