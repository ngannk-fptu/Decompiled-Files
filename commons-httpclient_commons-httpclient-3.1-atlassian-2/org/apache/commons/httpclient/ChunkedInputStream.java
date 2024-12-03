/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChunkedInputStream
extends InputStream {
    private InputStream in;
    private int chunkSize;
    private int pos;
    private boolean bof = true;
    private boolean eof = false;
    private boolean closed = false;
    private HttpMethod method = null;
    private static final Log LOG = LogFactory.getLog(ChunkedInputStream.class);

    public ChunkedInputStream(InputStream in, HttpMethod method) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("InputStream parameter may not be null");
        }
        this.in = in;
        this.method = method;
        this.pos = 0;
    }

    public ChunkedInputStream(InputStream in) throws IOException {
        this(in, null);
    }

    @Override
    public int read() throws IOException {
        int b;
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.eof) {
            return -1;
        }
        if (this.pos >= this.chunkSize) {
            this.nextChunk();
            if (this.eof) {
                return -1;
            }
        }
        if ((b = this.in.read()) != -1) {
            ++this.pos;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead;
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.eof) {
            return -1;
        }
        if (this.pos >= this.chunkSize) {
            this.nextChunk();
            if (this.eof) {
                return -1;
            }
        }
        if ((bytesRead = this.in.read(b, off, len = Math.min(len, this.chunkSize - this.pos))) != -1) {
            this.pos += bytesRead;
            return bytesRead;
        }
        throw new IOException("Truncated chunk");
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    private void readCRLF() throws IOException {
        int cr = this.in.read();
        int lf = this.in.read();
        if (cr != 13 || lf != 10) {
            throw new IOException("CRLF expected at end of chunk: " + cr + "/" + lf);
        }
    }

    private void nextChunk() throws IOException {
        if (!this.bof) {
            this.readCRLF();
        }
        this.chunkSize = ChunkedInputStream.getChunkSizeFromInputStream(this.in);
        this.bof = false;
        this.pos = 0;
        if (this.chunkSize == 0) {
            this.eof = true;
            this.parseTrailerHeaders();
        }
    }

    private static int getChunkSizeFromInputStream(InputStream in) throws IOException {
        int result;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int state = 0;
        block15: while (state != -1) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("chunked stream ended unexpectedly");
            }
            switch (state) {
                case 0: {
                    switch (b) {
                        case 13: {
                            state = 1;
                            continue block15;
                        }
                        case 34: {
                            state = 2;
                        }
                    }
                    baos.write(b);
                    continue block15;
                }
                case 1: {
                    if (b == 10) {
                        state = -1;
                        continue block15;
                    }
                    throw new IOException("Protocol violation: Unexpected single newline character in chunk size");
                }
                case 2: {
                    switch (b) {
                        case 92: {
                            b = in.read();
                            baos.write(b);
                            continue block15;
                        }
                        case 34: {
                            state = 0;
                        }
                    }
                    baos.write(b);
                    continue block15;
                }
            }
            throw new RuntimeException("assertion failed");
        }
        String dataString = EncodingUtil.getAsciiString(baos.toByteArray());
        int separator = dataString.indexOf(59);
        dataString = separator > 0 ? dataString.substring(0, separator).trim() : dataString.trim();
        try {
            result = Integer.parseInt(dataString.trim(), 16);
        }
        catch (NumberFormatException e) {
            throw new IOException("Bad chunk size: " + dataString);
        }
        return result;
    }

    private void parseTrailerHeaders() throws IOException {
        Header[] footers = null;
        try {
            String charset = "US-ASCII";
            if (this.method != null) {
                charset = this.method.getParams().getHttpElementCharset();
            }
            footers = HttpParser.parseHeaders(this.in, charset);
        }
        catch (HttpException e) {
            LOG.error((Object)"Error parsing trailer headers", (Throwable)e);
            IOException ioe = new IOException(e.getMessage());
            ExceptionUtil.initCause(ioe, e);
            throw ioe;
        }
        if (this.method != null) {
            for (int i = 0; i < footers.length; ++i) {
                this.method.addResponseFooter(footers[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (!this.eof) {
                    ChunkedInputStream.exhaustInputStream(this);
                }
            }
            finally {
                this.eof = true;
                this.closed = true;
            }
        }
    }

    static void exhaustInputStream(InputStream inStream) throws IOException {
        byte[] buffer = new byte[1024];
        while (inStream.read(buffer) >= 0) {
        }
    }
}

