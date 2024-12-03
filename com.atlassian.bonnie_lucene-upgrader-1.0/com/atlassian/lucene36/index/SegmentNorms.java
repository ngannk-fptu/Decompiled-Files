/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

final class SegmentNorms
implements Cloneable {
    static final byte[] NORMS_HEADER = new byte[]{78, 82, 77, -1};
    int refCount = 1;
    private SegmentNorms origNorm;
    private IndexInput in;
    private long normSeek;
    private AtomicInteger bytesRef;
    private byte[] bytes;
    private int number;
    boolean dirty;
    boolean rollbackDirty;
    private final SegmentReader owner;

    public SegmentNorms(IndexInput in, int number, long normSeek, SegmentReader owner) {
        this.in = in;
        this.number = number;
        this.normSeek = normSeek;
        this.owner = owner;
    }

    public synchronized void incRef() {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        ++this.refCount;
    }

    private void closeInput() throws IOException {
        if (this.in != null) {
            if (this.in != this.owner.singleNormStream) {
                this.in.close();
            } else if (this.owner.singleNormRef.decrementAndGet() == 0) {
                this.owner.singleNormStream.close();
                this.owner.singleNormStream = null;
            }
            this.in = null;
        }
    }

    public synchronized void decRef() throws IOException {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        if (--this.refCount == 0) {
            if (this.origNorm != null) {
                this.origNorm.decRef();
                this.origNorm = null;
            } else {
                this.closeInput();
            }
            if (this.bytes != null) {
                assert (this.bytesRef != null);
                this.bytesRef.decrementAndGet();
                this.bytes = null;
                this.bytesRef = null;
            } else assert (this.bytesRef == null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void bytes(byte[] bytesOut, int offset, int len) throws IOException {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        if (this.bytes != null) {
            assert (len <= this.owner.maxDoc());
            System.arraycopy(this.bytes, 0, bytesOut, offset, len);
        } else if (this.origNorm != null) {
            this.origNorm.bytes(bytesOut, offset, len);
        } else {
            IndexInput indexInput = this.in;
            synchronized (indexInput) {
                this.in.seek(this.normSeek);
                this.in.readBytes(bytesOut, offset, len, false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized byte[] bytes() throws IOException {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        if (this.bytes == null) {
            assert (this.bytesRef == null);
            if (this.origNorm != null) {
                this.bytes = this.origNorm.bytes();
                this.bytesRef = this.origNorm.bytesRef;
                this.bytesRef.incrementAndGet();
                this.origNorm.decRef();
                this.origNorm = null;
            } else {
                int count = this.owner.maxDoc();
                this.bytes = new byte[count];
                assert (this.in != null);
                IndexInput indexInput = this.in;
                synchronized (indexInput) {
                    this.in.seek(this.normSeek);
                    this.in.readBytes(this.bytes, 0, count, false);
                }
                this.bytesRef = new AtomicInteger(1);
                this.closeInput();
            }
        }
        return this.bytes;
    }

    AtomicInteger bytesRef() {
        return this.bytesRef;
    }

    public synchronized byte[] copyOnWrite() throws IOException {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        this.bytes();
        assert (this.bytes != null);
        assert (this.bytesRef != null);
        if (this.bytesRef.get() > 1) {
            assert (this.refCount == 1);
            AtomicInteger oldRef = this.bytesRef;
            this.bytes = this.owner.cloneNormBytes(this.bytes);
            this.bytesRef = new AtomicInteger(1);
            oldRef.decrementAndGet();
        }
        this.dirty = true;
        return this.bytes;
    }

    public synchronized Object clone() {
        SegmentNorms clone;
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0));
        try {
            clone = (SegmentNorms)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("unexpected CloneNotSupportedException", cnse);
        }
        clone.refCount = 1;
        if (this.bytes != null) {
            assert (this.bytesRef != null);
            assert (this.origNorm == null);
            clone.bytesRef.incrementAndGet();
        } else {
            assert (this.bytesRef == null);
            if (this.origNorm == null) {
                clone.origNorm = this;
            }
            clone.origNorm.incRef();
        }
        clone.in = null;
        return clone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reWrite(SegmentInfo si) throws IOException {
        assert (this.refCount > 0 && (this.origNorm == null || this.origNorm.refCount > 0)) : "refCount=" + this.refCount + " origNorm=" + this.origNorm;
        si.advanceNormGen(this.number);
        String normFileName = si.getNormFileName(this.number);
        IndexOutput out = this.owner.directory().createOutput(normFileName);
        boolean success = false;
        try {
            try {
                out.writeBytes(NORMS_HEADER, 0, NORMS_HEADER.length);
                out.writeBytes(this.bytes, this.owner.maxDoc());
            }
            finally {
                out.close();
            }
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.owner.directory().deleteFile(normFileName);
                }
                catch (Throwable t) {}
            }
        }
        this.dirty = false;
    }
}

