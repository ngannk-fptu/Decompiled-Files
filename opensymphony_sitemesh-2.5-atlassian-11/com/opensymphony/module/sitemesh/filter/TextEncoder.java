/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.filter;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class TextEncoder {
    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private static final boolean JDK14 = System.getProperty("java.version").startsWith("1.4") || System.getProperty("java.version").startsWith("1.5");

    public char[] encode(byte[] data, String encoding) throws IOException {
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        if (JDK14) {
            return this.get14Buffer(data, encoding);
        }
        return this.get13Buffer(data, encoding);
    }

    private char[] get13Buffer(byte[] data, String encoding) throws IOException {
        int i;
        CharArrayWriter out = null;
        out = new CharArrayWriter();
        InputStreamReader reader = encoding != null ? new InputStreamReader((InputStream)new ByteArrayInputStream(data), encoding) : new InputStreamReader(new ByteArrayInputStream(data));
        while ((i = reader.read()) != -1) {
            out.write(i);
        }
        return out.toCharArray();
    }

    private char[] get14Buffer(byte[] data, String encoding) throws IOException {
        CharBuffer cb;
        if (!Charset.isSupported(encoding)) {
            throw new IOException("Unsupported encoding " + encoding);
        }
        Charset charset = Charset.forName(encoding);
        CharsetDecoder cd = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        int en = (int)(cd.maxCharsPerByte() * (float)data.length);
        char[] ca = new char[en];
        ByteBuffer bb = ByteBuffer.wrap(data);
        CoderResult cr = cd.decode(bb, cb = CharBuffer.wrap(ca), true);
        if (!cr.isUnderflow()) {
            cr.throwException();
        }
        if (!(cr = cd.flush(cb)).isUnderflow()) {
            cr.throwException();
        }
        return this.trim(ca, cb.position());
    }

    private char[] trim(char[] ca, int len) {
        if (len == ca.length) {
            return ca;
        }
        char[] tca = new char[len];
        System.arraycopy(ca, 0, tca, 0, len);
        return tca;
    }
}

