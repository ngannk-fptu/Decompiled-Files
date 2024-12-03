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
import java.io.StringReader;

public class StringArrayReader
extends StringReader {
    private StringReader current;
    private String[] strings;
    protected final Object finalLock;
    private int currentSting;
    private int markedString;
    private int mark;
    private int next;

    public StringArrayReader(String[] stringArray) {
        super("");
        Validate.notNull((Object)stringArray, (String)"strings");
        this.lock = stringArray;
        this.finalLock = stringArray;
        this.strings = (String[])stringArray.clone();
        this.nextReader();
    }

    protected final Reader nextReader() {
        this.current = this.currentSting >= this.strings.length ? new EmptyReader() : new StringReader(this.strings[this.currentSting++]);
        this.next = 0;
        return this.current;
    }

    protected final void ensureOpen() throws IOException {
        if (this.strings == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void close() {
        super.close();
        this.strings = null;
        this.current.close();
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
            this.mark = this.next;
            this.markedString = this.currentSting;
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
            if (this.currentSting != this.markedString) {
                this.currentSting = this.markedString - 1;
                this.nextReader();
                this.current.skip(this.mark);
            } else {
                this.current.reset();
            }
            this.next = this.mark;
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read() throws IOException {
        Object object = this.finalLock;
        synchronized (object) {
            int n = this.current.read();
            if (n < 0 && this.currentSting < this.strings.length) {
                this.nextReader();
                return this.read();
            }
            ++this.next;
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
            if (n3 < 0 && this.currentSting < this.strings.length) {
                this.nextReader();
                return this.read(cArray, n, n2);
            }
            this.next += n3;
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
            if (l2 == 0L && this.currentSting < this.strings.length) {
                this.nextReader();
                return this.skip(l);
            }
            this.next = (int)((long)this.next + l2);
            return l2;
        }
    }
}

