/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.FloatMemoryArrayAccess;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;

public class FloatDiskDataStorage
extends DiskDataStorage {
    private static final long serialVersionUID = 1045290368963828503L;

    public FloatDiskDataStorage() throws ApfloatRuntimeException {
    }

    protected FloatDiskDataStorage(FloatDiskDataStorage floatDiskDataStorage, long offset, long length) {
        super(floatDiskDataStorage, offset, length);
    }

    @Override
    protected DataStorage implSubsequence(long offset, long length) throws ApfloatRuntimeException {
        return new FloatDiskDataStorage(this, offset + this.getOffset(), length);
    }

    @Override
    protected ArrayAccess implGetArray(int mode, long offset, int length) throws ApfloatRuntimeException {
        return new FloatDiskArrayAccess(mode, this.getOffset() + offset, length);
    }

    @Override
    protected ArrayAccess createArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new MemoryArrayAccess(mode, new float[columns * rows], startColumn, columns, rows);
    }

    @Override
    protected ArrayAccess createTransposedArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new TransposedMemoryArrayAccess(mode, new float[columns * rows], startColumn, columns, rows);
    }

    @Override
    public DataStorage.Iterator iterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
        if ((mode & 3) == 0) {
            throw new IllegalArgumentException("Illegal mode: " + mode);
        }
        return new BlockIterator(mode, startPosition, endPosition);
    }

    @Override
    protected int getUnitSize() {
        return 4;
    }

    private class FloatDiskArrayAccess
    extends FloatMemoryArrayAccess {
        private static final long serialVersionUID = 1750388414420962922L;
        private int mode;
        private long fileOffset;

        public FloatDiskArrayAccess(int mode, long fileOffset, int length) throws ApfloatRuntimeException {
            super(new float[length], 0, length);
            this.mode = mode;
            this.fileOffset = fileOffset;
            if ((mode & 1) != 0) {
                final float[] array = this.getFloatData();
                WritableByteChannel out = new WritableByteChannel(){
                    private int readPosition = 0;

                    @Override
                    public int write(ByteBuffer buffer) {
                        FloatBuffer src = buffer.asFloatBuffer();
                        int readLength = src.remaining();
                        src.get(array, this.readPosition, readLength);
                        this.readPosition += readLength;
                        buffer.position(buffer.position() + readLength * 4);
                        return readLength * 4;
                    }

                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean isOpen() {
                        return true;
                    }
                };
                FloatDiskDataStorage.this.transferTo(out, fileOffset * 4L, (long)length * 4L);
            }
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                final float[] array = this.getFloatData();
                ReadableByteChannel in = new ReadableByteChannel(){
                    private int writePosition = 0;

                    @Override
                    public int read(ByteBuffer buffer) {
                        FloatBuffer dst = buffer.asFloatBuffer();
                        int writeLength = dst.remaining();
                        dst.put(array, this.writePosition, writeLength);
                        this.writePosition += writeLength;
                        buffer.position(buffer.position() + writeLength * 4);
                        return writeLength * 4;
                    }

                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean isOpen() {
                        return true;
                    }
                };
                FloatDiskDataStorage.this.transferFrom(in, this.fileOffset * 4L, (long)array.length * 4L);
            }
            super.close();
        }
    }

    private class MemoryArrayAccess
    extends FloatMemoryArrayAccess {
        private static final long serialVersionUID = -3536582909010606907L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public MemoryArrayAccess(int mode, float[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                FloatDiskDataStorage.this.setArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class TransposedMemoryArrayAccess
    extends FloatMemoryArrayAccess {
        private static final long serialVersionUID = 898289922606519237L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public TransposedMemoryArrayAccess(int mode, float[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                FloatDiskDataStorage.this.setTransposedArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class BlockIterator
    extends DataStorage.AbstractIterator {
        private static final long serialVersionUID = 8503701548995236882L;
        private ArrayAccess arrayAccess;
        private float[] data;
        private int offset;
        private int remaining;

        public BlockIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(FloatDiskDataStorage.this, mode, startPosition, endPosition);
            this.arrayAccess = null;
            this.remaining = 0;
        }

        @Override
        public void next() throws IllegalStateException, ApfloatRuntimeException {
            this.checkLength();
            assert (this.remaining > 0);
            this.checkAvailable();
            this.offset += this.getIncrement();
            --this.remaining;
            if (this.remaining == 0) {
                this.close();
            }
            super.next();
        }

        @Override
        public float getFloat() throws IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            this.checkAvailable();
            return this.data[this.offset];
        }

        @Override
        public void setFloat(float value) throws IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            this.checkAvailable();
            this.data[this.offset] = value;
        }

        @Override
        public <T> T get(Class<T> type) throws UnsupportedOperationException, IllegalStateException {
            if (!type.equals(Float.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is float");
            }
            Float value = Float.valueOf(this.getFloat());
            return (T)value;
        }

        @Override
        public <T> void set(Class<T> type, T value) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException {
            if (!type.equals(Float.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is float");
            }
            if (!(value instanceof Float)) {
                throw new IllegalArgumentException("Unsupported value type " + value.getClass().getCanonicalName() + ", the only supported type is Float");
            }
            this.setFloat(((Float)value).floatValue());
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if (this.arrayAccess != null) {
                this.data = null;
                this.arrayAccess.close();
                this.arrayAccess = null;
            }
        }

        private void checkAvailable() throws ApfloatRuntimeException {
            if (this.arrayAccess == null) {
                boolean isForward = this.getIncrement() > 0;
                int length = (int)Math.min(this.getLength(), (long)(DiskDataStorage.getBlockSize() / 4));
                long offset = isForward ? this.getPosition() : this.getPosition() - (long)length + 1L;
                this.arrayAccess = FloatDiskDataStorage.this.getArray(this.getMode(), offset, length);
                this.data = this.arrayAccess.getFloatData();
                this.offset = this.arrayAccess.getOffset() + (isForward ? 0 : length - 1);
                this.remaining = length;
            }
        }
    }
}

