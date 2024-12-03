/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.DoubleMemoryArrayAccess;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;

public class DoubleDiskDataStorage
extends DiskDataStorage {
    private static final long serialVersionUID = 342871486421108657L;

    public DoubleDiskDataStorage() throws ApfloatRuntimeException {
    }

    protected DoubleDiskDataStorage(DoubleDiskDataStorage doubleDiskDataStorage, long offset, long length) {
        super(doubleDiskDataStorage, offset, length);
    }

    @Override
    protected DataStorage implSubsequence(long offset, long length) throws ApfloatRuntimeException {
        return new DoubleDiskDataStorage(this, offset + this.getOffset(), length);
    }

    @Override
    protected ArrayAccess implGetArray(int mode, long offset, int length) throws ApfloatRuntimeException {
        return new DoubleDiskArrayAccess(mode, this.getOffset() + offset, length);
    }

    @Override
    protected ArrayAccess createArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new MemoryArrayAccess(mode, new double[columns * rows], startColumn, columns, rows);
    }

    @Override
    protected ArrayAccess createTransposedArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new TransposedMemoryArrayAccess(mode, new double[columns * rows], startColumn, columns, rows);
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
        return 8;
    }

    private class DoubleDiskArrayAccess
    extends DoubleMemoryArrayAccess {
        private static final long serialVersionUID = -7097317279839657081L;
        private int mode;
        private long fileOffset;

        public DoubleDiskArrayAccess(int mode, long fileOffset, int length) throws ApfloatRuntimeException {
            super(new double[length], 0, length);
            this.mode = mode;
            this.fileOffset = fileOffset;
            if ((mode & 1) != 0) {
                final double[] array = this.getDoubleData();
                WritableByteChannel out = new WritableByteChannel(){
                    private int readPosition = 0;

                    @Override
                    public int write(ByteBuffer buffer) {
                        DoubleBuffer src = buffer.asDoubleBuffer();
                        int readLength = src.remaining();
                        src.get(array, this.readPosition, readLength);
                        this.readPosition += readLength;
                        buffer.position(buffer.position() + readLength * 8);
                        return readLength * 8;
                    }

                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean isOpen() {
                        return true;
                    }
                };
                DoubleDiskDataStorage.this.transferTo(out, fileOffset * 8L, (long)length * 8L);
            }
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                final double[] array = this.getDoubleData();
                ReadableByteChannel in = new ReadableByteChannel(){
                    private int writePosition = 0;

                    @Override
                    public int read(ByteBuffer buffer) {
                        DoubleBuffer dst = buffer.asDoubleBuffer();
                        int writeLength = dst.remaining();
                        dst.put(array, this.writePosition, writeLength);
                        this.writePosition += writeLength;
                        buffer.position(buffer.position() + writeLength * 8);
                        return writeLength * 8;
                    }

                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean isOpen() {
                        return true;
                    }
                };
                DoubleDiskDataStorage.this.transferFrom(in, this.fileOffset * 8L, (long)array.length * 8L);
            }
            super.close();
        }
    }

    private class MemoryArrayAccess
    extends DoubleMemoryArrayAccess {
        private static final long serialVersionUID = 3646716922431352928L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public MemoryArrayAccess(int mode, double[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                DoubleDiskDataStorage.this.setArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class TransposedMemoryArrayAccess
    extends DoubleMemoryArrayAccess {
        private static final long serialVersionUID = -3746109883682965310L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public TransposedMemoryArrayAccess(int mode, double[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                DoubleDiskDataStorage.this.setTransposedArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class BlockIterator
    extends DataStorage.AbstractIterator {
        private static final long serialVersionUID = -1996647087834590031L;
        private ArrayAccess arrayAccess;
        private double[] data;
        private int offset;
        private int remaining;

        public BlockIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(DoubleDiskDataStorage.this, mode, startPosition, endPosition);
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
        public double getDouble() throws IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            this.checkAvailable();
            return this.data[this.offset];
        }

        @Override
        public void setDouble(double value) throws IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            this.checkAvailable();
            this.data[this.offset] = value;
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
                int length = (int)Math.min(this.getLength(), (long)(DiskDataStorage.getBlockSize() / 8));
                long offset = isForward ? this.getPosition() : this.getPosition() - (long)length + 1L;
                this.arrayAccess = DoubleDiskDataStorage.this.getArray(this.getMode(), offset, length);
                this.data = this.arrayAccess.getDoubleData();
                this.offset = this.arrayAccess.getOffset() + (isForward ? 0 : length - 1);
                this.remaining = length;
            }
        }
    }
}

