/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class UCSReader
extends Reader {
    private Log log = LogFactory.getLog(UCSReader.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final short UCS2LE = 1;
    public static final short UCS2BE = 2;
    public static final short UCS4LE = 4;
    public static final short UCS4BE = 8;
    protected InputStream fInputStream;
    protected byte[] fBuffer;
    protected short fEncoding;

    public UCSReader(InputStream inputStream, short encoding) {
        this(inputStream, 8192, encoding);
    }

    public UCSReader(InputStream inputStream, int size, short encoding) {
        this.fInputStream = inputStream;
        this.fBuffer = new byte[size];
        this.fEncoding = encoding;
    }

    @Override
    public int read() throws IOException {
        int b0 = this.fInputStream.read() & 0xFF;
        if (b0 == 255) {
            return -1;
        }
        int b1 = this.fInputStream.read() & 0xFF;
        if (b1 == 255) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            int b2 = this.fInputStream.read() & 0xFF;
            if (b2 == 255) {
                return -1;
            }
            int b3 = this.fInputStream.read() & 0xFF;
            if (b3 == 255) {
                return -1;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("b0 is " + (b0 & 0xFF) + " b1 " + (b1 & 0xFF) + " b2 " + (b2 & 0xFF) + " b3 " + (b3 & 0xFF));
            }
            if (this.fEncoding == 8) {
                return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
            }
            return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
        }
        if (this.fEncoding == 2) {
            return (b0 << 8) + b1;
        }
        return (b1 << 8) + b0;
    }

    @Override
    public int read(char[] ch, int offset, int length) throws IOException {
        int numToRead;
        int count;
        int byteLength = length << (this.fEncoding >= 4 ? 2 : 1);
        if (byteLength > this.fBuffer.length) {
            byteLength = this.fBuffer.length;
        }
        if ((count = this.fInputStream.read(this.fBuffer, 0, byteLength)) == -1) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            numToRead = 4 - (count & 3) & 3;
            for (int i = 0; i < numToRead; ++i) {
                int charRead = this.fInputStream.read();
                if (charRead == -1) {
                    for (int j = i; j < numToRead; ++j) {
                        this.fBuffer[count + j] = 0;
                    }
                    break;
                }
                this.fBuffer[count + i] = (byte)charRead;
            }
            count += numToRead;
        } else {
            numToRead = count & 1;
            if (numToRead != 0) {
                ++count;
                int charRead = this.fInputStream.read();
                this.fBuffer[count] = charRead == -1 ? (byte)0 : (byte)charRead;
            }
        }
        int numChars = count >> (this.fEncoding >= 4 ? 2 : 1);
        int curPos = 0;
        for (int i = 0; i < numChars; ++i) {
            int b0 = this.fBuffer[curPos++] & 0xFF;
            int b1 = this.fBuffer[curPos++] & 0xFF;
            if (this.fEncoding >= 4) {
                int b2 = this.fBuffer[curPos++] & 0xFF;
                int b3 = this.fBuffer[curPos++] & 0xFF;
                if (this.fEncoding == 8) {
                    ch[offset + i] = (char)((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
                    continue;
                }
                ch[offset + i] = (char)((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
                continue;
            }
            ch[offset + i] = this.fEncoding == 2 ? (char)((b0 << 8) + b1) : (char)((b1 << 8) + b0);
        }
        return numChars;
    }

    @Override
    public long skip(long n) throws IOException {
        int charWidth = this.fEncoding >= 4 ? 2 : 1;
        long bytesSkipped = this.fInputStream.skip(n << charWidth);
        if ((bytesSkipped & (long)(charWidth | 1)) == 0L) {
            return bytesSkipped >> charWidth;
        }
        return (bytesSkipped >> charWidth) + 1L;
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.fInputStream.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        this.fInputStream.reset();
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }
}

