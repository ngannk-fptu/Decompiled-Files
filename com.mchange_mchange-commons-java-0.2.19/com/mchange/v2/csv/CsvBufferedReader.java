/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.csv;

import com.mchange.v2.csv.FastCsvUtils;
import com.mchange.v2.csv.MalformedCsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Stream;

public class CsvBufferedReader
extends BufferedReader {
    private BufferedReader inner;

    public CsvBufferedReader(BufferedReader bufferedReader) {
        super(bufferedReader);
        this.inner = bufferedReader;
    }

    @Override
    public String readLine() throws IOException {
        try {
            return FastCsvUtils.csvReadLine(this.inner);
        }
        catch (MalformedCsvException malformedCsvException) {
            throw new IOException("Badly formatted CSV file.", malformedCsvException);
        }
    }

    public String[] readSplitLine() throws IOException, MalformedCsvException {
        String string = this.readLine();
        return string == null ? null : FastCsvUtils.splitRecord(string);
    }

    @Override
    public int read() throws IOException {
        return this.inner.read();
    }

    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        return this.inner.read(cArray, n, n2);
    }

    @Override
    public long skip(long l) throws IOException {
        return this.inner.skip(l);
    }

    @Override
    public boolean ready() throws IOException {
        return this.inner.ready();
    }

    @Override
    public boolean markSupported() {
        return this.inner.markSupported();
    }

    @Override
    public void mark(int n) throws IOException {
        this.inner.mark(n);
    }

    @Override
    public void reset() throws IOException {
        this.inner.reset();
    }

    @Override
    public void close() throws IOException {
        this.inner.close();
    }

    @Override
    public Stream<String> lines() {
        throw new UnsupportedOperationException("lines() not yet implemented for CsvBufferedReader!");
    }
}

