/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.DoubleMemoryArrayAccess;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;

public class DoubleMemoryDataStorage
extends DataStorage {
    private static final long serialVersionUID = 5093781604796636929L;
    private double[] data;

    public DoubleMemoryDataStorage() {
        this.data = new double[0];
    }

    protected DoubleMemoryDataStorage(DoubleMemoryDataStorage doubleMemoryDataStorage, long offset, long length) {
        super(doubleMemoryDataStorage, offset, length);
        this.data = doubleMemoryDataStorage.data;
    }

    @Override
    public boolean isCached() {
        return true;
    }

    @Override
    protected DataStorage implSubsequence(long offset, long length) throws ApfloatRuntimeException {
        return new DoubleMemoryDataStorage(this, offset + this.getOffset(), length);
    }

    @Override
    protected void implCopyFrom(DataStorage dataStorage, long size) throws ApfloatRuntimeException {
        assert (size > 0L);
        assert (!this.isReadOnly());
        assert (!this.isSubsequenced());
        if (size > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Size too big for memory array: " + size);
        }
        if (dataStorage == this) {
            this.setSize(size);
            return;
        }
        this.data = new double[(int)size];
        ApfloatContext ctx = ApfloatContext.getContext();
        int readSize = (int)Math.min(size, dataStorage.getSize());
        int position = 0;
        int bufferSize = ctx.getBlockSize() / 8;
        while (readSize > 0) {
            int length = Math.min(bufferSize, readSize);
            try (ArrayAccess arrayAccess = dataStorage.getArray(1, position, length);){
                System.arraycopy(arrayAccess.getDoubleData(), arrayAccess.getOffset(), this.data, position, length);
            }
            readSize -= length;
            position += length;
        }
    }

    @Override
    protected long implGetSize() {
        return this.data.length;
    }

    @Override
    protected void implSetSize(long size) throws ApfloatRuntimeException {
        assert (size > 0L);
        assert (!this.isReadOnly());
        assert (!this.isSubsequenced());
        if (size == (long)this.data.length) {
            return;
        }
        if (size > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Size too big for memory array: " + size);
        }
        int newSize = (int)size;
        double[] newData = new double[newSize];
        System.arraycopy(this.data, 0, newData, 0, Math.min(this.data.length, newSize));
        this.data = newData;
    }

    @Override
    protected ArrayAccess implGetArray(int mode, long offset, int length) throws ApfloatRuntimeException {
        return new DoubleMemoryArrayAccess(this.data, (int)(offset + this.getOffset()), length);
    }

    @Override
    protected ArrayAccess implGetArray(int mode, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        throw new ApfloatInternalException("Method not implemented - would be sub-optimal; change the apfloat configuration settings");
    }

    @Override
    protected ArrayAccess implGetTransposedArray(int mode, int startColumn, int columns, int rows) throws ApfloatRuntimeException {
        throw new ApfloatInternalException("Method not implemented - would be sub-optimal; change the apfloat configuration settings");
    }

    @Override
    public DataStorage.Iterator iterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        ReadWriteIterator iterator;
        switch (mode & 3) {
            case 1: {
                iterator = new ReadOnlyIterator(startPosition, endPosition);
                break;
            }
            case 2: {
                iterator = new WriteOnlyIterator(startPosition, endPosition);
                break;
            }
            case 3: {
                iterator = new ReadWriteIterator(startPosition, endPosition);
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal mode: " + mode);
            }
        }
        return iterator;
    }

    private class ReadOnlyIterator
    extends ReadWriteIterator {
        private static final long serialVersionUID = 5449985546703735328L;

        public ReadOnlyIterator(long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(1, startPosition, endPosition);
        }

        @Override
        public void setDouble(double value) throws IllegalStateException {
            throw new IllegalStateException("Not a writable iterator");
        }
    }

    private class WriteOnlyIterator
    extends ReadWriteIterator {
        private static final long serialVersionUID = 3758519654059499404L;

        public WriteOnlyIterator(long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(2, startPosition, endPosition);
        }

        @Override
        public double getDouble() throws IllegalStateException {
            throw new IllegalStateException("Not a readable iterator");
        }
    }

    private class ReadWriteIterator
    extends DataStorage.AbstractIterator {
        private static final long serialVersionUID = -9012199261873349608L;
        private double[] data;
        private int position;
        private int length;

        public ReadWriteIterator(long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            this(3, startPosition, endPosition);
        }

        protected ReadWriteIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(DoubleMemoryDataStorage.this, mode, startPosition, endPosition);
            this.data = DoubleMemoryDataStorage.this.data;
            this.position = (int)this.getPosition() + (int)DoubleMemoryDataStorage.this.getOffset();
            this.length = (int)this.getLength();
        }

        @Override
        public boolean hasNext() {
            return this.length > 0;
        }

        @Override
        public void next() throws IllegalStateException {
            this.checkLength();
            this.position += this.getIncrement();
            --this.length;
        }

        @Override
        public double getDouble() throws IllegalStateException {
            this.checkLength();
            return this.data[this.position];
        }

        @Override
        public void setDouble(double value) throws IllegalStateException {
            this.checkLength();
            this.data[this.position] = value;
        }

        @Override
        public <T> T get(Class<T> type) throws UnsupportedOperationException, IllegalStateException {
            if (!type.equals(Double.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is double");
            }
            Double value = this.getDouble();
            return (T)value;
        }

        @Override
        public <T> void set(Class<T> type, T value) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException {
            if (!type.equals(Double.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is double");
            }
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("Unsupported value type " + value.getClass().getCanonicalName() + ", the only supported type is Double");
            }
            this.setDouble((Double)value);
        }

        @Override
        protected void checkLength() throws IllegalStateException {
            if (this.length == 0) {
                throw new IllegalStateException("At the end of iterator");
            }
        }
    }
}

