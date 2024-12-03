/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public final class XmlHeaderAwareReader
extends Reader {
    private final InputStreamReader reader;
    private final double version;
    private static final String KEY_ENCODING = "encoding";
    private static final String KEY_VERSION = "version";
    private static final String XML_TOKEN = "?xml";
    private static final int STATE_BOM = 0;
    private static final int STATE_START = 1;
    private static final int STATE_AWAIT_XML_HEADER = 2;
    private static final int STATE_ATTR_NAME = 3;
    private static final int STATE_ATTR_VALUE = 4;

    public XmlHeaderAwareReader(InputStream in) throws UnsupportedEncodingException, IOException {
        PushbackInputStream[] pin = new PushbackInputStream[]{in instanceof PushbackInputStream ? (PushbackInputStream)in : new PushbackInputStream(in, 64)};
        Map header = this.getHeader(pin);
        this.version = Double.parseDouble((String)header.get(KEY_VERSION));
        this.reader = new InputStreamReader((InputStream)pin[0], (String)header.get(KEY_ENCODING));
    }

    private Map getHeader(PushbackInputStream[] in) throws IOException {
        HashMap<String, String> header = new HashMap<String, String>();
        header.put(KEY_ENCODING, "UTF-8");
        header.put(KEY_VERSION, "1.0");
        int state = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        int i = 0;
        char ch = '\u0000';
        char valueEnd = '\u0000';
        StringBuffer name = new StringBuffer();
        StringBuffer value = new StringBuffer();
        boolean escape = false;
        while (i != -1 && (i = in[0].read()) != -1) {
            out.write(i);
            ch = (char)i;
            switch (state) {
                case 0: {
                    if (ch == '\u00ef' && out.size() == 1 || ch == '\u00bb' && out.size() == 2 || ch == '\u00bf' && out.size() == 3) {
                        if (ch != '\u00bf') break;
                        out.reset();
                        state = 1;
                        break;
                    }
                    if (out.size() > 1) {
                        i = -1;
                        break;
                    }
                    state = 1;
                }
                case 1: {
                    if (Character.isWhitespace(ch)) break;
                    if (ch == '<') {
                        state = 2;
                        break;
                    }
                    i = -1;
                    break;
                }
                case 2: {
                    if (!Character.isWhitespace(ch)) {
                        name.append(Character.toLowerCase(ch));
                        if (XML_TOKEN.startsWith(name.substring(0))) break;
                        i = -1;
                        break;
                    }
                    if (name.toString().equals(XML_TOKEN)) {
                        state = 3;
                        name.setLength(0);
                        break;
                    }
                    i = -1;
                    break;
                }
                case 3: {
                    if (!Character.isWhitespace(ch)) {
                        if (ch == '=') {
                            state = 4;
                            break;
                        }
                        if (Character.isLetter(ch = Character.toLowerCase(ch))) {
                            name.append(ch);
                            break;
                        }
                        i = -1;
                        break;
                    }
                    if (name.length() <= 0) break;
                    i = -1;
                    break;
                }
                case 4: {
                    if (valueEnd == '\u0000') {
                        if (ch == '\"' || ch == '\'') {
                            valueEnd = ch;
                            break;
                        }
                        i = -1;
                        break;
                    }
                    if (ch == '\\' && !escape) {
                        escape = true;
                        break;
                    }
                    if (ch == valueEnd && !escape) {
                        valueEnd = '\u0000';
                        state = 3;
                        header.put(name.toString(), value.toString());
                        name.setLength(0);
                        value.setLength(0);
                        break;
                    }
                    escape = false;
                    if (ch != '\n') {
                        value.append(ch);
                        break;
                    }
                    i = -1;
                }
            }
        }
        byte[] pushbackData = out.toByteArray();
        i = pushbackData.length;
        while (i-- > 0) {
            byte b = pushbackData[i];
            try {
                in[0].unread(b);
            }
            catch (IOException ex) {
                in[0] = new PushbackInputStream(in[0], ++i);
            }
        }
        return header;
    }

    public String getEncoding() {
        return this.reader.getEncoding();
    }

    public double getVersion() {
        return this.version;
    }

    public void mark(int readAheadLimit) throws IOException {
        this.reader.mark(readAheadLimit);
    }

    public boolean markSupported() {
        return this.reader.markSupported();
    }

    public int read() throws IOException {
        return this.reader.read();
    }

    public int read(char[] cbuf, int offset, int length) throws IOException {
        return this.reader.read(cbuf, offset, length);
    }

    public int read(char[] cbuf) throws IOException {
        return this.reader.read(cbuf);
    }

    public boolean ready() throws IOException {
        return this.reader.ready();
    }

    public void reset() throws IOException {
        this.reader.reset();
    }

    public long skip(long n) throws IOException {
        return this.reader.skip(n);
    }

    public void close() throws IOException {
        this.reader.close();
    }

    public boolean equals(Object obj) {
        return this.reader.equals(obj);
    }

    public int hashCode() {
        return this.reader.hashCode();
    }

    public String toString() {
        return this.reader.toString();
    }
}

