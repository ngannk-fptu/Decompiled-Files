/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteBufferUtils
 */
package org.apache.tomcat.util.net;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.ByteBufferUtils;

public class SocketBufferHandler {
    static SocketBufferHandler EMPTY = new SocketBufferHandler(0, 0, false){

        @Override
        public void expand(int newSize) {
        }

        @Override
        public void unReadReadBuffer(ByteBuffer returnedData) {
        }
    };
    private volatile boolean readBufferConfiguredForWrite = true;
    private volatile ByteBuffer readBuffer;
    private volatile boolean writeBufferConfiguredForWrite = true;
    private volatile ByteBuffer writeBuffer;
    private final boolean direct;

    public SocketBufferHandler(int readBufferSize, int writeBufferSize, boolean direct) {
        this.direct = direct;
        if (direct) {
            this.readBuffer = ByteBuffer.allocateDirect(readBufferSize);
            this.writeBuffer = ByteBuffer.allocateDirect(writeBufferSize);
        } else {
            this.readBuffer = ByteBuffer.allocate(readBufferSize);
            this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
        }
    }

    public void configureReadBufferForWrite() {
        this.setReadBufferConfiguredForWrite(true);
    }

    public void configureReadBufferForRead() {
        this.setReadBufferConfiguredForWrite(false);
    }

    private void setReadBufferConfiguredForWrite(boolean readBufferConFiguredForWrite) {
        if (this.readBufferConfiguredForWrite != readBufferConFiguredForWrite) {
            if (readBufferConFiguredForWrite) {
                int remaining = this.readBuffer.remaining();
                if (remaining == 0) {
                    this.readBuffer.clear();
                } else {
                    this.readBuffer.compact();
                }
            } else {
                this.readBuffer.flip();
            }
            this.readBufferConfiguredForWrite = readBufferConFiguredForWrite;
        }
    }

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public boolean isReadBufferEmpty() {
        if (this.readBufferConfiguredForWrite) {
            return this.readBuffer.position() == 0;
        }
        return this.readBuffer.remaining() == 0;
    }

    public void unReadReadBuffer(ByteBuffer returnedData) {
        if (this.isReadBufferEmpty()) {
            this.configureReadBufferForWrite();
            this.readBuffer.put(returnedData);
        } else {
            int bytesReturned = returnedData.remaining();
            if (this.readBufferConfiguredForWrite) {
                int i;
                if (this.readBuffer.position() + bytesReturned > this.readBuffer.capacity()) {
                    throw new BufferOverflowException();
                }
                for (i = 0; i < this.readBuffer.position(); ++i) {
                    this.readBuffer.put(i + bytesReturned, this.readBuffer.get(i));
                }
                for (i = 0; i < bytesReturned; ++i) {
                    this.readBuffer.put(i, returnedData.get());
                }
                this.readBuffer.position(this.readBuffer.position() + bytesReturned);
            } else {
                int insertOffset;
                int i;
                int shiftRequired = bytesReturned - this.readBuffer.position();
                if (shiftRequired > 0) {
                    if (this.readBuffer.capacity() - this.readBuffer.limit() < shiftRequired) {
                        throw new BufferOverflowException();
                    }
                    int oldLimit = this.readBuffer.limit();
                    this.readBuffer.limit(oldLimit + shiftRequired);
                    for (i = this.readBuffer.position(); i < oldLimit; ++i) {
                        this.readBuffer.put(i + shiftRequired, this.readBuffer.get(i));
                    }
                } else {
                    shiftRequired = 0;
                }
                for (i = insertOffset = this.readBuffer.position() + shiftRequired - bytesReturned; i < bytesReturned + insertOffset; ++i) {
                    this.readBuffer.put(i, returnedData.get());
                }
                this.readBuffer.position(insertOffset);
            }
        }
    }

    public void configureWriteBufferForWrite() {
        this.setWriteBufferConfiguredForWrite(true);
    }

    public void configureWriteBufferForRead() {
        this.setWriteBufferConfiguredForWrite(false);
    }

    private void setWriteBufferConfiguredForWrite(boolean writeBufferConfiguredForWrite) {
        if (this.writeBufferConfiguredForWrite != writeBufferConfiguredForWrite) {
            if (writeBufferConfiguredForWrite) {
                int remaining = this.writeBuffer.remaining();
                if (remaining == 0) {
                    this.writeBuffer.clear();
                } else {
                    this.writeBuffer.compact();
                    this.writeBuffer.position(remaining);
                    this.writeBuffer.limit(this.writeBuffer.capacity());
                }
            } else {
                this.writeBuffer.flip();
            }
            this.writeBufferConfiguredForWrite = writeBufferConfiguredForWrite;
        }
    }

    public boolean isWriteBufferWritable() {
        if (this.writeBufferConfiguredForWrite) {
            return this.writeBuffer.hasRemaining();
        }
        return this.writeBuffer.remaining() == 0;
    }

    public ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public boolean isWriteBufferEmpty() {
        if (this.writeBufferConfiguredForWrite) {
            return this.writeBuffer.position() == 0;
        }
        return this.writeBuffer.remaining() == 0;
    }

    public void reset() {
        this.readBuffer.clear();
        this.readBufferConfiguredForWrite = true;
        this.writeBuffer.clear();
        this.writeBufferConfiguredForWrite = true;
    }

    public void expand(int newSize) {
        this.configureReadBufferForWrite();
        this.readBuffer = ByteBufferUtils.expand((ByteBuffer)this.readBuffer, (int)newSize);
        this.configureWriteBufferForWrite();
        this.writeBuffer = ByteBufferUtils.expand((ByteBuffer)this.writeBuffer, (int)newSize);
    }

    public void free() {
        if (this.direct) {
            ByteBufferUtils.cleanDirectBuffer((ByteBuffer)this.readBuffer);
            ByteBufferUtils.cleanDirectBuffer((ByteBuffer)this.writeBuffer);
        }
    }
}

