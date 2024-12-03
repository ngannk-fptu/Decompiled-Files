/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.EmptyReader;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundReader
extends Reader {
    private Reader current;
    private List<Reader> readers;
    protected final Object finalLock;
    protected final boolean markSupported;
    private int currentReader;
    private int markedReader;
    private int mark;
    private int mNext;

    public CompoundReader(Iterator<Reader> iterator) {
        super(Validate.notNull(iterator, (String)"readers"));
        this.finalLock = iterator;
        this.readers = new ArrayList<Reader>();
        boolean bl = true;
        while (iterator.hasNext()) {
            Reader reader = iterator.next();
            if (reader == null) {
                throw new NullPointerException("readers cannot contain null-elements");
            }
            this.readers.add(reader);
            bl = bl && reader.markSupported();
        }
        this.markSupported = bl;
        this.current = this.nextReader();
    }

    protected final Reader nextReader() {
        this.current = this.currentReader >= this.readers.size() ? new EmptyReader() : this.readers.get(this.currentReader++);
        this.mNext = 0;
        return this.current;
    }

    protected final void ensureOpen() throws IOException {
        if (this.readers == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void close() throws IOException {
        for (Reader reader : this.readers) {
            reader.close();
        }
        this.readers = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void mark(int n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("Read limit < 0");
        }
        Object object = this.finalLock;
        synchronized (object) {
            this.ensureOpen();
            this.mark = this.mNext;
            this.markedReader = this.currentReader;
            this.current.mark(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reset() throws IOException {
        Object object = this.finalLock;
        synchronized (object) {
            this.ensureOpen();
            if (this.currentReader != this.markedReader) {
                for (int i = this.currentReader; i >= this.markedReader; --i) {
                    this.readers.get(i).reset();
                }
                this.currentReader = this.markedReader - 1;
                this.nextReader();
            }
            this.current.reset();
            this.mNext = this.mark;
        }
    }

    @Override
    public boolean markSupported() {
        return this.markSupported;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read() throws IOException {
        Object object = this.finalLock;
        synchronized (object) {
            int n = this.current.read();
            if (n < 0 && this.currentReader < this.readers.size()) {
                this.nextReader();
                return this.read();
            }
            ++this.mNext;
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        Object object = this.finalLock;
        synchronized (object) {
            int n3 = this.current.read(cArray, n, n2);
            if (n3 < 0 && this.currentReader < this.readers.size()) {
                this.nextReader();
                return this.read(cArray, n, n2);
            }
            this.mNext += n3;
            return n3;
        }
    }

    @Override
    public boolean ready() throws IOException {
        return this.current.ready();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long skip(long l) throws IOException {
        Object object = this.finalLock;
        synchronized (object) {
            long l2 = this.current.skip(l);
            if (l2 == 0L && this.currentReader < this.readers.size()) {
                this.nextReader();
                return this.skip(l);
            }
            this.mNext = (int)((long)this.mNext + l2);
            return l2;
        }
    }
}

