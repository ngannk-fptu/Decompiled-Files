/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Properties
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.est;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.bouncycastle.est.CTEBase64InputStream;
import org.bouncycastle.est.CTEChunkedInputStream;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.HttpUtil;
import org.bouncycastle.est.LimitedSource;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class ESTResponse {
    private final ESTRequest originalRequest;
    private final HttpUtil.Headers headers;
    private final byte[] lineBuffer;
    private final Source source;
    private String HttpVersion;
    private int statusCode;
    private String statusMessage;
    private InputStream inputStream;
    private Long contentLength;
    private long read = 0L;
    private Long absoluteReadLimit;
    private static final Long ZERO = 0L;

    public long getAbsoluteReadLimit() {
        return this.absoluteReadLimit == null ? Long.MAX_VALUE : this.absoluteReadLimit;
    }

    public ESTResponse(ESTRequest originalRequest, Source source) throws IOException {
        Set opts;
        this.originalRequest = originalRequest;
        this.source = source;
        if (source instanceof LimitedSource) {
            this.absoluteReadLimit = ((LimitedSource)((Object)source)).getAbsoluteReadLimit();
        }
        this.inputStream = (opts = Properties.asKeySet((String)"org.bouncycastle.debug.est")).contains("input") || opts.contains("all") ? new PrintingInputStream(source.getInputStream()) : source.getInputStream();
        this.headers = new HttpUtil.Headers();
        this.lineBuffer = new byte[1024];
        this.process();
    }

    private void process() throws IOException {
        this.HttpVersion = this.readStringIncluding(' ');
        this.statusCode = Integer.parseInt(this.readStringIncluding(' '));
        this.statusMessage = this.readStringIncluding('\n');
        String line = this.readStringIncluding('\n');
        while (line.length() > 0) {
            int i = line.indexOf(58);
            if (i > -1) {
                String k = Strings.toLowerCase((String)line.substring(0, i).trim());
                this.headers.add(k, line.substring(i + 1).trim());
            }
            line = this.readStringIncluding('\n');
        }
        boolean chunked = this.headers.getFirstValueOrEmpty("Transfer-Encoding").equalsIgnoreCase("chunked");
        this.contentLength = chunked ? Long.valueOf(0L) : this.getContentLength();
        if (this.statusCode == 204 || this.statusCode == 202) {
            if (this.contentLength == null) {
                this.contentLength = 0L;
            } else if (this.statusCode == 204 && this.contentLength > 0L) {
                throw new IOException("Got HTTP status 204 but Content-length > 0.");
            }
        }
        if (this.contentLength == null) {
            throw new IOException("No Content-length header.");
        }
        if (this.contentLength.equals(ZERO) && !chunked) {
            this.inputStream = new InputStream(){

                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
        if (this.contentLength < 0L) {
            throw new IOException("Server returned negative content length: " + this.absoluteReadLimit);
        }
        if (this.absoluteReadLimit != null && this.contentLength >= this.absoluteReadLimit) {
            throw new IOException("Content length longer than absolute read limit: " + this.absoluteReadLimit + " Content-Length: " + this.contentLength);
        }
        this.inputStream = this.wrapWithCounter(this.inputStream, this.absoluteReadLimit);
        if (chunked) {
            this.inputStream = new CTEChunkedInputStream(this.inputStream);
        }
        if ("base64".equalsIgnoreCase(this.getHeader("content-transfer-encoding"))) {
            this.inputStream = chunked ? new CTEBase64InputStream(this.inputStream) : new CTEBase64InputStream(this.inputStream, this.contentLength);
        }
    }

    public String getHeader(String key) {
        return this.headers.getFirstValue(key);
    }

    public String getHeaderOrEmpty(String key) {
        return this.headers.getFirstValueOrEmpty(key);
    }

    protected InputStream wrapWithCounter(final InputStream in, final Long absoluteReadLimit) {
        return new InputStream(){

            @Override
            public int read() throws IOException {
                int i = in.read();
                if (i > -1) {
                    ESTResponse.this.read++;
                    if (absoluteReadLimit != null && ESTResponse.this.read >= absoluteReadLimit) {
                        throw new IOException("Absolute Read Limit exceeded: " + absoluteReadLimit);
                    }
                }
                return i;
            }

            @Override
            public void close() throws IOException {
                if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength - 1L > ESTResponse.this.read) {
                    throw new IOException("Stream closed before limit fully read, Read: " + ESTResponse.this.read + " ContentLength: " + ESTResponse.this.contentLength);
                }
                if (in.available() > 0) {
                    throw new IOException("Stream closed with extra content in pipe that exceeds content length.");
                }
                in.close();
            }
        };
    }

    protected String readStringIncluding(char until) throws IOException {
        int j;
        int c = 0;
        do {
            j = this.inputStream.read();
            this.lineBuffer[c++] = (byte)j;
            if (c < this.lineBuffer.length) continue;
            throw new IOException("Server sent line > " + this.lineBuffer.length);
        } while (j != until && j > -1);
        if (j == -1) {
            throw new EOFException();
        }
        return new String(this.lineBuffer, 0, c).trim();
    }

    public ESTRequest getOriginalRequest() {
        return this.originalRequest;
    }

    public HttpUtil.Headers getHeaders() {
        return this.headers;
    }

    public String getHttpVersion() {
        return this.HttpVersion;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public Source getSource() {
        return this.source;
    }

    public Long getContentLength() {
        String v = this.headers.getFirstValue("Content-Length");
        if (v == null) {
            return null;
        }
        try {
            return Long.parseLong(v);
        }
        catch (RuntimeException nfe) {
            throw new RuntimeException("Content Length: '" + v + "' invalid. " + nfe.getMessage());
        }
    }

    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        this.source.close();
    }

    private static class PrintingInputStream
    extends InputStream {
        private final InputStream src;

        private PrintingInputStream(InputStream src) {
            this.src = src;
        }

        @Override
        public int read() throws IOException {
            int i = this.src.read();
            return i;
        }

        @Override
        public int available() throws IOException {
            return this.src.available();
        }

        @Override
        public void close() throws IOException {
            this.src.close();
        }
    }
}

