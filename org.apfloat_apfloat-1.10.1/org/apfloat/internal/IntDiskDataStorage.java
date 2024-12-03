/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.IntMemoryArrayAccess;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;

public class IntDiskDataStorage
extends DiskDataStorage {
    private static final long serialVersionUID = -1540087135754114721L;

    public IntDiskDataStorage() throws ApfloatRuntimeException {
    }

    protected IntDiskDataStorage(IntDiskDataStorage intDiskDataStorage, long offset, long length) {
        super(intDiskDataStorage, offset, length);
    }

    @Override
    protected DataStorage implSubsequence(long offset, long length) throws ApfloatRuntimeException {
        return new IntDiskDataStorage(this, offset + this.getOffset(), length);
    }

    @Override
    protected ArrayAccess implGetArray(int mode, long offset, int length) throws ApfloatRuntimeException {
        return new IntDiskArrayAccess(mode, this.getOffset() + offset, length);
    }

    @Override
    protected ArrayAccess createArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new MemoryArrayAccess(mode, new int[columns * rows], startColumn, columns, rows);
    }

    @Override
    protected ArrayAccess createTransposedArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new TransposedMemoryArrayAccess(mode, new int[columns * rows], startColumn, columns, rows);
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

    private class IntDiskArrayAccess
    extends IntMemoryArrayAccess {
        private static final long serialVersionUID = -88509093904437138L;
        private int mode;
        private long fileOffset;

        public IntDiskArrayAccess(int mode, long fileOffset, int length) throws ApfloatRuntimeException {
            super(new int[length], 0, length);
            this.mode = mode;
            this.fileOffset = fileOffset;
            if ((mode & 1) != 0) {
                final int[] array = this.getIntData();
                WritableByteChannel out = new WritableByteChannel(){
                    private int readPosition = 0;

                    @Override
                    public int write(ByteBuffer buffer) {
                        IntBuffer src = buffer.asIntBuffer();
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
                IntDiskDataStorage.this.transferTo(out, fileOffset * 4L, (long)length * 4L);
            }
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                final int[] array = this.getIntData();
                ReadableByteChannel in = new ReadableByteChannel(){
                    private int writePosition = 0;

                    @Override
                    public int read(ByteBuffer buffer) {
                        IntBuffer dst = buffer.asIntBuffer();
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
                IntDiskDataStorage.this.transferFrom(in, this.fileOffset * 4L, (long)array.length * 4L);
            }
            super.close();
        }
    }

    private class MemoryArrayAccess
    extends IntMemoryArrayAccess {
        private static final long serialVersionUID = 7690849230285450035L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public MemoryArrayAccess(int mode, int[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                IntDiskDataStorage.this.setArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class TransposedMemoryArrayAccess
    extends IntMemoryArrayAccess {
        private static final long serialVersionUID = 2990517367865486151L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public TransposedMemoryArrayAccess(int mode, int[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                IntDiskDataStorage.this.setTransposedArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class BlockIterator
    extends DataStorage.AbstractIterator {
        private static final long serialVersionUID = 4187202582650284101L;
        private ArrayAccess arrayAccess;
        private int[] data;
        private int offset;
        private int remaining;

        public BlockIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(IntDiskDataStorage.this, mode, startPosition, endPosition);
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
        public int getInt() throws IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            this.checkAvailable();
            return this.data[this.offset];
        }

        @Override
        public void setInt(int value) throws IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            this.checkAvailable();
            this.data[this.offset] = value;
        }

        @Override
        public <T> T get(Class<T> type) throws UnsupportedOperationException, IllegalStateException {
            if (!type.equals(Integer.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is int");
            }
            Integer value = this.getInt();
            return (T)value;
        }

        @Override
        public <T> void set(Class<T> type, T value) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException {
            if (!type.equals(Integer.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is int");
            }
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("Unsupported value type " + value.getClass().getCanonicalName() + ", the only supported type is Integer");
            }
            this.setInt((Integer)value);
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
                this.arrayAccess = IntDiskDataStorage.this.getArray(this.getMode(), offset, length);
                this.data = this.arrayAccess.getIntData();
                this.offset = this.arrayAccess.getOffset() + (isForward ? 0 : length - 1);
                this.remaining = length;
            }
        }
    }
}

