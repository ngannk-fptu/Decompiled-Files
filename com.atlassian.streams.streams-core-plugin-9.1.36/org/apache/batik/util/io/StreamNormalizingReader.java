/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.util.EncodingUtilities;
import org.apache.batik.util.io.ASCIIDecoder;
import org.apache.batik.util.io.CharDecoder;
import org.apache.batik.util.io.GenericDecoder;
import org.apache.batik.util.io.ISO_8859_1Decoder;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.util.io.UTF16Decoder;
import org.apache.batik.util.io.UTF8Decoder;

public class StreamNormalizingReader
extends NormalizingReader {
    protected CharDecoder charDecoder;
    protected int nextChar = -1;
    protected int line = 1;
    protected int column;
    protected static final Map charDecoderFactories = new HashMap(11);

    public StreamNormalizingReader(InputStream is) throws IOException {
        this(is, null);
    }

    public StreamNormalizingReader(InputStream is, String enc) throws IOException {
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        this.charDecoder = this.createCharDecoder(is, enc);
    }

    public StreamNormalizingReader(Reader r) throws IOException {
        this.charDecoder = new GenericDecoder(r);
    }

    protected StreamNormalizingReader() {
    }

    @Override
    public int read() throws IOException {
        int result = this.nextChar;
        if (result != -1) {
            this.nextChar = -1;
            if (result == 13) {
                this.column = 0;
                ++this.line;
            } else {
                ++this.column;
            }
            return result;
        }
        result = this.charDecoder.readChar();
        switch (result) {
            case 13: {
                this.column = 0;
                ++this.line;
                int c = this.charDecoder.readChar();
                if (c == 10) {
                    return 10;
                }
                this.nextChar = c;
                return 10;
            }
            case 10: {
                this.column = 0;
                ++this.line;
            }
        }
        return result;
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    public void close() throws IOException {
        this.charDecoder.dispose();
        this.charDecoder = null;
    }

    protected CharDecoder createCharDecoder(InputStream is, String enc) throws IOException {
        CharDecoderFactory cdf = (CharDecoderFactory)charDecoderFactories.get(enc.toUpperCase());
        if (cdf != null) {
            return cdf.createCharDecoder(is);
        }
        String e = EncodingUtilities.javaEncoding(enc);
        if (e == null) {
            e = enc;
        }
        return new GenericDecoder(is, e);
    }

    static {
        ASCIIDecoderFactory cdf = new ASCIIDecoderFactory();
        charDecoderFactories.put("ASCII", cdf);
        charDecoderFactories.put("US-ASCII", cdf);
        charDecoderFactories.put("ISO-8859-1", new ISO_8859_1DecoderFactory());
        charDecoderFactories.put("UTF-8", new UTF8DecoderFactory());
        charDecoderFactories.put("UTF-16", new UTF16DecoderFactory());
    }

    protected static class UTF16DecoderFactory
    implements CharDecoderFactory {
        protected UTF16DecoderFactory() {
        }

        @Override
        public CharDecoder createCharDecoder(InputStream is) throws IOException {
            return new UTF16Decoder(is);
        }
    }

    protected static class UTF8DecoderFactory
    implements CharDecoderFactory {
        protected UTF8DecoderFactory() {
        }

        @Override
        public CharDecoder createCharDecoder(InputStream is) throws IOException {
            return new UTF8Decoder(is);
        }
    }

    protected static class ISO_8859_1DecoderFactory
    implements CharDecoderFactory {
        protected ISO_8859_1DecoderFactory() {
        }

        @Override
        public CharDecoder createCharDecoder(InputStream is) throws IOException {
            return new ISO_8859_1Decoder(is);
        }
    }

    protected static class ASCIIDecoderFactory
    implements CharDecoderFactory {
        protected ASCIIDecoderFactory() {
        }

        @Override
        public CharDecoder createCharDecoder(InputStream is) throws IOException {
            return new ASCIIDecoder(is);
        }
    }

    protected static interface CharDecoderFactory {
        public CharDecoder createCharDecoder(InputStream var1) throws IOException;
    }
}

