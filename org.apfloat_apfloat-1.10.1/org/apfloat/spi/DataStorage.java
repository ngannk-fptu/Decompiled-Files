/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import java.io.Serializable;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public abstract class DataStorage
implements Serializable {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = 3;
    private static final long serialVersionUID = 1862028601696578467L;
    private long offset;
    private long length;
    private DataStorage originalDataStorage;
    private boolean isReadOnly;
    private boolean isSubsequenced;

    protected DataStorage() {
        this.offset = 0L;
        this.length = 0L;
        this.originalDataStorage = null;
        this.isReadOnly = false;
        this.isSubsequenced = false;
    }

    protected DataStorage(DataStorage dataStorage, long offset, long length) {
        this.offset = offset;
        this.length = length;
        this.originalDataStorage = dataStorage;
    }

    public final DataStorage subsequence(long offset, long length) throws IllegalArgumentException, ApfloatRuntimeException {
        if (offset < 0L || length <= 0L || offset + length < 0L || offset + length > this.getSize()) {
            throw new IllegalArgumentException("Requested subsequence out of range: offset=" + offset + ", length=" + length + ", available=" + this.getSize());
        }
        this.setSubsequenced();
        if (offset == 0L && length == this.getSize()) {
            return this;
        }
        return this.implSubsequence(offset, length);
    }

    protected abstract DataStorage implSubsequence(long var1, long var3) throws ApfloatRuntimeException;

    public final void copyFrom(DataStorage dataStorage) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        this.copyFrom(dataStorage, dataStorage.getSize());
    }

    public final void copyFrom(DataStorage dataStorage, long size) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if (size <= 0L) {
            throw new IllegalArgumentException("Illegal size: " + size);
        }
        if (this.isReadOnly()) {
            throw new IllegalStateException("Cannot copy to read-only object");
        }
        if (this.isSubsequenced()) {
            throw new IllegalStateException("Cannot copy to when subsequences exist");
        }
        this.implCopyFrom(dataStorage, size);
    }

    protected abstract void implCopyFrom(DataStorage var1, long var2) throws ApfloatRuntimeException;

    public final long getSize() throws ApfloatRuntimeException {
        if (this.isReadOnly() || this.isSubsequenced()) {
            return this.length;
        }
        return this.implGetSize();
    }

    protected abstract long implGetSize() throws ApfloatRuntimeException;

    public final void setSize(long size) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if (size <= 0L) {
            throw new IllegalArgumentException("Illegal size: " + size);
        }
        if (this.isReadOnly()) {
            throw new IllegalStateException("Cannot set size of read-only object");
        }
        if (this.isSubsequenced()) {
            throw new IllegalStateException("Cannot set size when subsequences exist");
        }
        this.implSetSize(size);
    }

    protected abstract void implSetSize(long var1) throws ApfloatRuntimeException;

    public final boolean isReadOnly() {
        if (this.originalDataStorage == null) {
            return this.isReadOnly;
        }
        return this.originalDataStorage.isReadOnly();
    }

    public final void setReadOnly() throws ApfloatRuntimeException {
        if (this.isReadOnly()) {
            return;
        }
        if (!this.isSubsequenced()) {
            this.length = this.implGetSize();
        }
        if (this.originalDataStorage == null) {
            this.isReadOnly = true;
        } else {
            this.originalDataStorage.setReadOnly();
        }
    }

    public final ArrayAccess getArray(int mode, long offset, int length) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if (this.isReadOnly() && (mode & 2) != 0) {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }
        if (offset < 0L || length < 0 || offset + (long)length < 0L || offset + (long)length > this.getSize()) {
            throw new IllegalArgumentException("Requested block out of range: offset=" + offset + ", length=" + length + ", available=" + this.getSize());
        }
        return this.implGetArray(mode, offset, length);
    }

    protected abstract ArrayAccess implGetArray(int var1, long var2, int var4) throws ApfloatRuntimeException;

    public final ArrayAccess getArray(int mode, int startColumn, int columns, int rows) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if (this.isReadOnly() && (mode & 2) != 0) {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }
        long size = (long)columns * (long)rows;
        if (startColumn < 0 || columns < 0 || rows < 0 || startColumn + columns < 0 || (long)(startColumn + columns) * (long)rows > this.getSize()) {
            throw new IllegalArgumentException("Requested block out of range: startColumn=" + startColumn + ", columns=" + columns + ", rows=" + rows + ", available=" + this.getSize());
        }
        if (size > Integer.MAX_VALUE) {
            throw new ApfloatRuntimeException("Block too large to fit in an array: " + size);
        }
        return this.implGetArray(mode, startColumn, columns, rows);
    }

    protected abstract ArrayAccess implGetArray(int var1, int var2, int var3, int var4) throws ApfloatRuntimeException;

    public final ArrayAccess getTransposedArray(int mode, int startColumn, int columns, int rows) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if (this.isReadOnly() && (mode & 2) != 0) {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }
        long size = (long)columns * (long)rows;
        if (startColumn < 0 || columns < 0 || rows < 0 || startColumn + columns < 0 || (long)(startColumn + columns) * (long)rows > this.getSize()) {
            throw new IllegalArgumentException("Requested block out of range: startColumn=" + startColumn + ", columns=" + columns + ", rows=" + rows + ", available=" + this.getSize());
        }
        if (size > Integer.MAX_VALUE) {
            throw new ApfloatRuntimeException("Block too large to fit in an array: " + size);
        }
        return this.implGetTransposedArray(mode, startColumn, columns, rows);
    }

    protected abstract ArrayAccess implGetTransposedArray(int var1, int var2, int var3, int var4) throws ApfloatRuntimeException;

    public abstract Iterator iterator(int var1, long var2, long var4) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException;

    public final boolean isSubsequenced() {
        if (this.originalDataStorage == null) {
            return this.isSubsequenced;
        }
        return true;
    }

    public abstract boolean isCached();

    protected final long getOffset() {
        return this.offset;
    }

    private void setSubsequenced() throws ApfloatRuntimeException {
        if (!this.isSubsequenced()) {
            if (!this.isReadOnly()) {
                this.length = this.implGetSize();
            }
            this.isSubsequenced = true;
        }
    }

    protected abstract class AbstractIterator
    extends Iterator {
        private static final long serialVersionUID = 1668346231773868058L;
        private int mode;
        private int increment;
        private long position;
        private long length;

        protected AbstractIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            if (startPosition < 0L || endPosition < 0L || startPosition > DataStorage.this.getSize() || endPosition > DataStorage.this.getSize()) {
                throw new IllegalArgumentException("Requested block out of range: startPosition=" + startPosition + ", endPosition=" + endPosition + ", available=" + DataStorage.this.getSize());
            }
            if (DataStorage.this.isReadOnly() && (mode & 2) != 0) {
                throw new IllegalStateException("Write access requested for read-only data storage");
            }
            this.mode = mode;
            if (endPosition >= startPosition) {
                this.position = startPosition;
                this.length = endPosition - startPosition;
                this.increment = 1;
            } else {
                this.position = startPosition - 1L;
                this.length = startPosition - endPosition;
                this.increment = -1;
            }
        }

        @Override
        public boolean hasNext() {
            return this.length > 0L;
        }

        @Override
        public void next() throws IllegalStateException, ApfloatRuntimeException {
            this.checkLength();
            this.position += (long)this.increment;
            --this.length;
        }

        @Override
        public int getInt() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            return super.getInt();
        }

        @Override
        public long getLong() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            return super.getLong();
        }

        @Override
        public float getFloat() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            return super.getFloat();
        }

        @Override
        public double getDouble() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            return super.getDouble();
        }

        @Override
        public void setInt(int value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            super.setInt(value);
        }

        @Override
        public void setLong(long value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            super.setLong(value);
        }

        @Override
        public void setFloat(float value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            super.setFloat(value);
        }

        @Override
        public void setDouble(double value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            super.setDouble(value);
        }

        protected void checkGet() throws IllegalStateException {
            this.checkLength();
            if ((this.mode & 1) == 0) {
                throw new IllegalStateException("Not a readable iterator");
            }
        }

        protected void checkSet() throws IllegalStateException {
            this.checkLength();
            if ((this.mode & 2) == 0) {
                throw new IllegalStateException("Not a writable iterator");
            }
        }

        protected void checkLength() throws IllegalStateException {
            if (this.length == 0L) {
                throw new IllegalStateException("At the end of iterator");
            }
        }

        protected int getMode() {
            return this.mode;
        }

        protected long getPosition() {
            return this.position;
        }

        protected long getLength() {
            return this.length;
        }

        protected int getIncrement() {
            return this.increment;
        }
    }

    public static abstract class Iterator
    implements Serializable,
    AutoCloseable {
        private static final long serialVersionUID = 7155668655967297483L;

        protected Iterator() {
        }

        public boolean hasNext() {
            return false;
        }

        public void next() throws IllegalStateException, ApfloatRuntimeException {
            throw new IllegalStateException("Not implemented");
        }

        public int getInt() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            return this.get(Integer.TYPE);
        }

        public long getLong() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            return this.get(Long.TYPE);
        }

        public float getFloat() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            return this.get(Float.TYPE).floatValue();
        }

        public double getDouble() throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            return this.get(Double.TYPE);
        }

        public void setInt(int value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.set(Integer.TYPE, value);
        }

        public void setLong(long value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.set(Long.TYPE, value);
        }

        public void setFloat(float value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.set(Float.TYPE, Float.valueOf(value));
        }

        public void setDouble(double value) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            this.set(Double.TYPE, value);
        }

        public <T> T get(Class<T> type) throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public <T> void set(Class<T> type, T value) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void close() throws ApfloatRuntimeException {
        }
    }
}

