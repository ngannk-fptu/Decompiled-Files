/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.catalina.connector.InputBuffer;

public class CoyoteReader
extends BufferedReader {
    private static final char[] LINE_SEP = new char[]{'\r', '\n'};
    private static final int MAX_LINE_LENGTH = 4096;
    protected InputBuffer ib;
    protected char[] lineBuffer = null;

    public CoyoteReader(InputBuffer ib) {
        super(ib, 1);
        this.ib = ib;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    void clear() {
        this.ib = null;
    }

    @Override
    public void close() throws IOException {
        this.ib.close();
    }

    @Override
    public int read() throws IOException {
        return this.ib.read();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        return this.ib.read(cbuf, 0, cbuf.length);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.ib.read(cbuf, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.ib.skip(n);
    }

    @Override
    public boolean ready() throws IOException {
        return this.ib.ready();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.ib.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        this.ib.reset();
    }

    @Override
    public String readLine() throws IOException {
        if (this.lineBuffer == null) {
            this.lineBuffer = new char[4096];
        }
        String result = null;
        int pos = 0;
        int end = -1;
        int skip = -1;
        StringBuilder aggregator = null;
        while (end < 0) {
            this.mark(4096);
            while (pos < 4096 && end < 0) {
                int nRead = this.read(this.lineBuffer, pos, 4096 - pos);
                if (nRead < 0) {
                    if (pos == 0 && aggregator == null) {
                        return null;
                    }
                    end = pos;
                    skip = pos;
                }
                for (int i = pos; i < pos + nRead && end < 0; ++i) {
                    if (this.lineBuffer[i] == LINE_SEP[0]) {
                        end = i;
                        skip = i + 1;
                        char nextchar = i == pos + nRead - 1 ? (char)this.read() : this.lineBuffer[i + 1];
                        if (nextchar != LINE_SEP[1]) continue;
                        ++skip;
                        continue;
                    }
                    if (this.lineBuffer[i] != LINE_SEP[1]) continue;
                    end = i;
                    skip = i + 1;
                }
                if (nRead <= 0) continue;
                pos += nRead;
            }
            if (end < 0) {
                if (aggregator == null) {
                    aggregator = new StringBuilder();
                }
                aggregator.append(this.lineBuffer);
                pos = 0;
                continue;
            }
            this.reset();
            this.skip(skip);
        }
        if (aggregator == null) {
            result = new String(this.lineBuffer, 0, end);
        } else {
            aggregator.append(this.lineBuffer, 0, end);
            result = aggregator.toString();
        }
        return result;
    }
}

