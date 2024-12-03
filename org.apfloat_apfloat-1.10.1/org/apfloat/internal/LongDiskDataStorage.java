/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.LongMemoryArrayAccess;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;

public class LongDiskDataStorage
extends DiskDataStorage {
    private static final long serialVersionUID = 4741507089425158620L;

    public LongDiskDataStorage() throws ApfloatRuntimeException {
    }

    protected LongDiskDataStorage(LongDiskDataStorage longDiskDataStorage, long offset, long length) {
        super(longDiskDataStorage, offset, length);
    }

    @Override
    protected DataStorage implSubsequence(long offset, long length) throws ApfloatRuntimeException {
        return new LongDiskDataStorage(this, offset + this.getOffset(), length);
    }

    @Override
    protected ArrayAccess implGetArray(int mode, long offset, int length) throws ApfloatRuntimeException {
        return new LongDiskArrayAccess(mode, this.getOffset() + offset, length);
    }

    @Override
    protected ArrayAccess createArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new MemoryArrayAccess(mode, new long[columns * rows], startColumn, columns, rows);
    }

    @Override
    protected ArrayAccess createTransposedArrayAccess(int mode, int startColumn, int columns, int rows) {
        return new TransposedMemoryArrayAccess(mode, new long[columns * rows], startColumn, columns, rows);
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

    private class LongDiskArrayAccess
    extends LongMemoryArrayAccess {
        private static final long serialVersionUID = -2591640502422276852L;
        private int mode;
        private long fileOffset;

        public LongDiskArrayAccess(int mode, long fileOffset, int length) throws ApfloatRuntimeException {
            super(new long[length], 0, length);
            this.mode = mode;
            this.fileOffset = fileOffset;
            if ((mode & 1) != 0) {
                final long[] array = this.getLongData();
                WritableByteChannel out = new WritableByteChannel(){
                    private int readPosition = 0;

                    @Override
                    public int write(ByteBuffer buffer) {
                        LongBuffer src = buffer.asLongBuffer();
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
                LongDiskDataStorage.this.transferTo(out, fileOffset * 8L, (long)length * 8L);
            }
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                final long[] array = this.getLongData();
                ReadableByteChannel in = new ReadableByteChannel(){
                    private int writePosition = 0;

                    @Override
                    public int read(ByteBuffer buffer) {
                        LongBuffer dst = buffer.asLongBuffer();
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
                LongDiskDataStorage.this.transferFrom(in, this.fileOffset * 8L, (long)array.length * 8L);
            }
            super.close();
        }
    }

    private class MemoryArrayAccess
    extends LongMemoryArrayAccess {
        private static final long serialVersionUID = -1573539652919953016L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public MemoryArrayAccess(int mode, long[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                LongDiskDataStorage.this.setArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class TransposedMemoryArrayAccess
    extends LongMemoryArrayAccess {
        private static final long serialVersionUID = -455915044370886962L;
        private int mode;
        private int startColumn;
        private int columns;
        private int rows;

        public TransposedMemoryArrayAccess(int mode, long[] data, int startColumn, int columns, int rows) {
            super(data, 0, data.length);
            this.mode = mode;
            this.startColumn = startColumn;
            this.columns = columns;
            this.rows = rows;
        }

        @Override
        public void close() throws ApfloatRuntimeException {
            if ((this.mode & 2) != 0 && this.getData() != null) {
                LongDiskDataStorage.this.setTransposedArray(this, this.startColumn, this.columns, this.rows);
            }
            super.close();
        }
    }

    private class BlockIterator
    extends DataStorage.AbstractIterator {
        private static final long serialVersionUID = -2804905180796718735L;
        private ArrayAccess arrayAccess;
        private long[] data;
        private int offset;
        private int remaining;

        public BlockIterator(int mode, long startPosition, long endPosition) throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException {
            super(LongDiskDataStorage.this, mode, startPosition, endPosition);
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
        public long getLong() throws IllegalStateException, ApfloatRuntimeException {
            this.checkGet();
            this.checkAvailable();
            return this.data[this.offset];
        }

        @Override
        public void setLong(long value) throws IllegalStateException, ApfloatRuntimeException {
            this.checkSet();
            this.checkAvailable();
            this.data[this.offset] = value;
        }

        @Override
        public <T> T get(Class<T> type) throws UnsupportedOperationException, IllegalStateException {
            if (!type.equals(Long.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is long");
            }
            Long value = this.getLong();
            return (T)value;
        }

        @Override
        public <T> void set(Class<T> type, T value) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException {
            if (!type.equals(Long.TYPE)) {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is long");
            }
            if (!(value instanceof Long)) {
                throw new IllegalArgumentException("Unsupported value type " + value.getClass().getCanonicalName() + ", the only supported type is Long");
            }
            this.setLong((Long)value);
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
                this.arrayAccess = LongDiskDataStorage.this.getArray(this.getMode(), offset, length);
                this.data = this.arrayAccess.getLongData();
                this.offset = this.arrayAccess.getOffset() + (isForward ? 0 : length - 1);
                this.remaining = length;
            }
        }
    }
}

