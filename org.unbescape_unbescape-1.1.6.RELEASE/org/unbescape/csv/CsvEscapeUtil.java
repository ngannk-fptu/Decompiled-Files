/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

final class CsvEscapeUtil {
    private static final char DOUBLE_QUOTE = '\"';
    private static final char[] TWO_DOUBLE_QUOTES = "\"\"".toCharArray();

    private CsvEscapeUtil() {
    }

    static String escape(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') continue;
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
                strBuilder.append('\"');
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            readOffset = i + 1;
            if (c == '\"') {
                strBuilder.append(TWO_DOUBLE_QUOTES);
                continue;
            }
            strBuilder.append(c);
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        strBuilder.append('\"');
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer) throws IOException {
        if (reader == null) {
            return;
        }
        int doQuote = -1;
        int bufferSize = 0;
        char[] buffer = new char[10];
        int read = reader.read(buffer, 0, buffer.length);
        if (read < 0) {
            return;
        }
        while (doQuote < 0 && read >= 0) {
            int i = bufferSize;
            bufferSize += read;
            while (doQuote < 0 && i < bufferSize) {
                char cq;
                if ((cq = buffer[i++]) >= 'a' && cq <= 'z' || cq >= 'A' && cq <= 'Z' || cq >= '0' && cq <= '9') continue;
                doQuote = 1;
                break;
            }
            if (doQuote >= 0 || read < 0) continue;
            if (bufferSize == buffer.length) {
                char[] newBuffer = new char[buffer.length + buffer.length / 2];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
            read = reader.read(buffer, bufferSize, buffer.length - bufferSize);
        }
        if ((doQuote = Math.max(doQuote, 0)) == 1) {
            writer.write(34);
        }
        if (bufferSize > 0) {
            for (int i = 0; i < bufferSize; ++i) {
                char c = buffer[i];
                if (c == '\"') {
                    writer.write(TWO_DOUBLE_QUOTES);
                    continue;
                }
                writer.write(c);
            }
        }
        if (read >= 0) {
            int c1 = -1;
            int c2 = reader.read();
            while (c2 >= 0) {
                c1 = c2;
                c2 = reader.read();
                if (c1 == 34) {
                    writer.write(TWO_DOUBLE_QUOTES);
                    continue;
                }
                writer.write(c1);
            }
        }
        if (doQuote == 1) {
            writer.write(34);
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') continue;
            if (readOffset == offset) {
                writer.write(34);
            }
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            readOffset = i + 1;
            if (c == '\"') {
                writer.write(TWO_DOUBLE_QUOTES);
                continue;
            }
            writer.write(c);
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
        if (readOffset > offset) {
            writer.write(34);
        }
    }

    static String unescape(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        int referenceOffset = 0;
        boolean isQuoted = false;
        for (int i = 0; i < max; ++i) {
            char c = text.charAt(i);
            if (i > 0 && c != '\"' || c != '\"') continue;
            if (i == 0) {
                if (i + 1 >= max || text.charAt(max - 1) != '\"') continue;
                isQuoted = true;
                referenceOffset = i + 1;
                readOffset = i + 1;
                continue;
            }
            if (isQuoted && i + 2 < max) {
                char c1 = text.charAt(i + 1);
                if (c1 == '\"') {
                    referenceOffset = i + 1;
                }
            } else {
                if (!isQuoted || i + 1 < max) continue;
                referenceOffset = i + 1;
            }
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (referenceOffset >= max) continue;
            strBuilder.append(c);
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void unescape(Reader reader, Writer writer) throws IOException {
        int c1;
        if (reader == null) {
            return;
        }
        boolean isQuoted = false;
        int c2 = reader.read();
        if (c2 < 0) {
            return;
        }
        if (c2 == 34) {
            c1 = c2;
            c2 = reader.read();
            if (c2 < 0) {
                writer.write(c1);
                return;
            }
            isQuoted = true;
        }
        while (c2 >= 0) {
            c1 = c2;
            c2 = reader.read();
            if (c1 != 34) {
                writer.write(c1);
                continue;
            }
            if (c2 < 0) {
                if (isQuoted) continue;
                writer.write(c1);
                continue;
            }
            if (c2 == 34) {
                writer.write(34);
                c1 = c2;
                c2 = reader.read();
                continue;
            }
            writer.write(34);
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int referenceOffset = offset;
        boolean isQuoted = false;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (i > offset && c != '\"' || c != '\"') continue;
            if (i == offset) {
                if (i + 1 >= max || text[max - 1] != '\"') continue;
                isQuoted = true;
                referenceOffset = i + 1;
                readOffset = i + 1;
                continue;
            }
            if (isQuoted && i + 2 < max) {
                char c1 = text[i + 1];
                if (c1 == '\"') {
                    referenceOffset = i + 1;
                }
            } else {
                if (!isQuoted || i + 1 < max) continue;
                referenceOffset = i + 1;
            }
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            i = referenceOffset;
            readOffset = i + 1;
            if (referenceOffset >= max) continue;
            writer.write(c);
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }
}

