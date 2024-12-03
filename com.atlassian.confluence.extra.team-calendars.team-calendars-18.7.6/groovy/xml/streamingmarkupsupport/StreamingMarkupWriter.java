/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml.streamingmarkupsupport;

import groovy.io.EncodingAwareBufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StreamingMarkupWriter
extends Writer {
    protected final Writer writer;
    protected final String encoding;
    protected boolean encodingKnown;
    protected final CharsetEncoder encoder;
    protected boolean writingAttribute = false;
    protected boolean haveHighSurrogate = false;
    protected StringBuilder surrogatePair = new StringBuilder(2);
    private boolean useDoubleQuotes;
    private final Writer escapedWriter = new Writer(){

        @Override
        public void close() throws IOException {
            StreamingMarkupWriter.this.close();
        }

        @Override
        public void flush() throws IOException {
            StreamingMarkupWriter.this.flush();
        }

        @Override
        public void write(int c) throws IOException {
            if (c == 60) {
                StreamingMarkupWriter.this.writer.write("&lt;");
            } else if (c == 62) {
                StreamingMarkupWriter.this.writer.write("&gt;");
            } else if (c == 38) {
                StreamingMarkupWriter.this.writer.write("&amp;");
            } else {
                StreamingMarkupWriter.this.write(c);
            }
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            while (len-- > 0) {
                this.write(cbuf[off++]);
            }
        }

        public void setWritingAttribute(boolean writingAttribute) {
            StreamingMarkupWriter.this.writingAttribute = writingAttribute;
        }

        public Writer escaped() {
            return StreamingMarkupWriter.this.escapedWriter;
        }

        public Writer unescaped() {
            return StreamingMarkupWriter.this;
        }
    };

    public StreamingMarkupWriter(Writer writer, String encoding) {
        this(writer, encoding, false);
    }

    public StreamingMarkupWriter(Writer writer, String encoding, boolean useDoubleQuotes) {
        this.useDoubleQuotes = useDoubleQuotes;
        this.writer = writer;
        if (encoding != null) {
            this.encoding = encoding;
            this.encodingKnown = true;
        } else if (writer instanceof OutputStreamWriter) {
            this.encoding = StreamingMarkupWriter.getNormalizedEncoding(((OutputStreamWriter)writer).getEncoding());
            this.encodingKnown = true;
        } else if (writer instanceof EncodingAwareBufferedWriter) {
            this.encoding = StreamingMarkupWriter.getNormalizedEncoding(((EncodingAwareBufferedWriter)writer).getEncoding());
            this.encodingKnown = true;
        } else {
            this.encoding = "US-ASCII";
            this.encodingKnown = false;
        }
        this.encoder = Charset.forName(this.encoding).newEncoder();
    }

    private static String getNormalizedEncoding(String unnormalized) {
        return Charset.forName(unnormalized).name();
    }

    public StreamingMarkupWriter(Writer writer) {
        this(writer, null);
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void write(int c) throws IOException {
        if (c >= 56320 && c <= 57343) {
            this.surrogatePair.append((char)c);
            if (this.encoder.canEncode(this.surrogatePair)) {
                this.writer.write(this.surrogatePair.toString());
            } else {
                this.writer.write("&#x");
                this.writer.write(Integer.toHexString(65536 + ((this.surrogatePair.charAt(0) & 0x3FF) << 10) + (c & 0x3FF)));
                this.writer.write(59);
            }
            this.haveHighSurrogate = false;
            this.surrogatePair.setLength(0);
        } else {
            if (this.haveHighSurrogate) {
                this.haveHighSurrogate = false;
                this.surrogatePair.setLength(0);
                throw new IOException("High Surrogate not followed by Low Surrogate");
            }
            if (c >= 55296 && c <= 56319) {
                this.surrogatePair.append((char)c);
                this.haveHighSurrogate = true;
            } else if (!this.encoder.canEncode((char)c)) {
                this.writer.write("&#x");
                this.writer.write(Integer.toHexString(c));
                this.writer.write(59);
            } else if (c == 39 && this.writingAttribute && !this.useDoubleQuotes) {
                this.writer.write("&apos;");
            } else if (c == 34 && this.writingAttribute && this.useDoubleQuotes) {
                this.writer.write("&quot;");
            } else if (c == 10 && this.writingAttribute) {
                this.writer.write("&#10;");
            } else if (c == 13 && this.writingAttribute) {
                this.writer.write("&#13;");
            } else if (c == 9 && this.writingAttribute) {
                this.writer.write("&#09;");
            } else {
                this.writer.write(c);
            }
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        while (len-- > 0) {
            this.write(cbuf[off++]);
        }
    }

    public void setWritingAttribute(boolean writingAttribute) {
        this.writingAttribute = writingAttribute;
    }

    public Writer escaped() {
        return this.escapedWriter;
    }

    public Writer unescaped() {
        return this;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public boolean getEncodingKnown() {
        return this.encodingKnown;
    }
}

