/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.SharedInputStream
 */
package org.bouncycastle.mail.smime.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.mail.internet.SharedInputStream;

public class SharedFileInputStream
extends FilterInputStream
implements SharedInputStream {
    private final SharedFileInputStream _parent;
    private final File _file;
    private final long _start;
    private final long _length;
    private long _position;
    private long _markedPosition;
    private List _subStreams = new LinkedList();

    public SharedFileInputStream(String fileName) throws IOException {
        this(new File(fileName));
    }

    public SharedFileInputStream(File file) throws IOException {
        this(file, 0L, file.length());
    }

    private SharedFileInputStream(File file, long start, long length) throws IOException {
        super(new BufferedInputStream(new FileInputStream(file)));
        this._parent = null;
        this._file = file;
        this._start = start;
        this._length = length;
        this.in.skip(start);
    }

    private SharedFileInputStream(SharedFileInputStream parent, long start, long length) throws IOException {
        super(new BufferedInputStream(new FileInputStream(parent._file)));
        this._parent = parent;
        this._file = parent._file;
        this._start = start;
        this._length = length;
        this.in.skip(start);
    }

    public long getPosition() {
        return this._position;
    }

    public InputStream newStream(long start, long finish) {
        try {
            SharedFileInputStream stream = finish < 0L ? (this._length > 0L ? new SharedFileInputStream(this, this._start + start, this._length - start) : (this._length == 0L ? new SharedFileInputStream(this, this._start + start, 0L) : new SharedFileInputStream(this, this._start + start, -1L))) : new SharedFileInputStream(this, this._start + start, finish - start);
            this._subStreams.add(stream);
            return stream;
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to create shared stream: " + e);
        }
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        int ch;
        int count;
        if (len == 0) {
            return 0;
        }
        for (count = 0; count < len && (ch = this.read()) >= 0; ++count) {
            buf[off + count] = (byte)ch;
        }
        if (count == 0) {
            return -1;
        }
        return count;
    }

    @Override
    public int read() throws IOException {
        if (this._position == this._length) {
            return -1;
        }
        ++this._position;
        return this.in.read();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public long skip(long n) throws IOException {
        long count;
        for (count = 0L; count != n && this.read() >= 0; ++count) {
        }
        return count;
    }

    @Override
    public void mark(int readLimit) {
        this._markedPosition = this._position;
        this.in.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        this._position = this._markedPosition;
        this.in.reset();
    }

    public SharedFileInputStream getRoot() {
        if (this._parent != null) {
            return this._parent.getRoot();
        }
        return this;
    }

    public void dispose() throws IOException {
        Iterator it = this._subStreams.iterator();
        while (it.hasNext()) {
            try {
                ((SharedFileInputStream)it.next()).dispose();
            }
            catch (IOException iOException) {}
        }
        this.in.close();
    }
}

