/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.eclipse.jetty.util.compression.CompressionPool$Entry
 *  org.eclipse.jetty.util.compression.InflaterPool
 */
package org.eclipse.jetty.http;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.util.compression.InflaterPool;

public class GZIPContentDecoder
implements Destroyable {
    private static final long UINT_MAX = 0xFFFFFFFFL;
    private final List<ByteBuffer> _inflateds = new ArrayList<ByteBuffer>();
    private final ByteBufferPool _pool;
    private final int _bufferSize;
    private final boolean _useDirectBuffers;
    private CompressionPool.Entry _inflaterEntry;
    private Inflater _inflater;
    private State _state;
    private int _size;
    private long _value;
    private byte _flags;
    private ByteBuffer _inflated;

    public GZIPContentDecoder() {
        this(null, 2048);
    }

    public GZIPContentDecoder(int bufferSize) {
        this(null, bufferSize);
    }

    public GZIPContentDecoder(ByteBufferPool pool, int bufferSize) {
        this(new InflaterPool(0, true), pool, bufferSize);
    }

    public GZIPContentDecoder(ByteBufferPool pool, int bufferSize, boolean useDirectBuffers) {
        this(new InflaterPool(0, true), pool, bufferSize, useDirectBuffers);
    }

    public GZIPContentDecoder(InflaterPool inflaterPool, ByteBufferPool pool, int bufferSize) {
        this(inflaterPool, pool, bufferSize, false);
    }

    public GZIPContentDecoder(InflaterPool inflaterPool, ByteBufferPool pool, int bufferSize, boolean useDirectBuffers) {
        this._inflaterEntry = inflaterPool.acquire();
        this._inflater = (Inflater)this._inflaterEntry.get();
        this._bufferSize = bufferSize;
        this._pool = pool;
        this._useDirectBuffers = useDirectBuffers;
        this.reset();
    }

    public ByteBuffer decode(ByteBuffer compressed) {
        this.decodeChunks(compressed);
        if (this._inflateds.isEmpty()) {
            if (BufferUtil.isEmpty((ByteBuffer)this._inflated) || this._state == State.CRC || this._state == State.ISIZE) {
                return BufferUtil.EMPTY_BUFFER;
            }
            ByteBuffer result = this._inflated;
            this._inflated = null;
            return result;
        }
        this._inflateds.add(this._inflated);
        this._inflated = null;
        int length = this._inflateds.stream().mapToInt(Buffer::remaining).sum();
        ByteBuffer result = this.acquire(length);
        for (ByteBuffer buffer : this._inflateds) {
            BufferUtil.append((ByteBuffer)result, (ByteBuffer)buffer);
            this.release(buffer);
        }
        this._inflateds.clear();
        return result;
    }

    protected boolean decodedChunk(ByteBuffer chunk) {
        if (this._inflated == null) {
            this._inflated = chunk;
        } else if (BufferUtil.space((ByteBuffer)this._inflated) >= chunk.remaining()) {
            BufferUtil.append((ByteBuffer)this._inflated, (ByteBuffer)chunk);
            this.release(chunk);
        } else {
            this._inflateds.add(this._inflated);
            this._inflated = chunk;
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void decodeChunks(ByteBuffer compressed) {
        ByteBuffer buffer = null;
        try {
            block29: while (true) {
                block43: {
                    switch (this._state) {
                        case INITIAL: {
                            this._state = State.ID;
                            break;
                        }
                        case FLAGS: {
                            if ((this._flags & 4) == 4) {
                                this._state = State.EXTRA_LENGTH;
                                this._size = 0;
                                this._value = 0L;
                                break;
                            }
                            if ((this._flags & 8) == 8) {
                                this._state = State.NAME;
                                break;
                            }
                            if ((this._flags & 0x10) == 16) {
                                this._state = State.COMMENT;
                                break;
                            }
                            if ((this._flags & 2) == 2) {
                                this._state = State.HCRC;
                                this._size = 0;
                                this._value = 0L;
                                break;
                            }
                            this._state = State.DATA;
                            continue block29;
                        }
                        case DATA: {
                            break block43;
                        }
                    }
                    if (compressed.hasRemaining()) {
                        byte currByte = compressed.get();
                        switch (this._state) {
                            case ID: {
                                this._value += ((long)currByte & 0xFFL) << 8 * this._size;
                                ++this._size;
                                if (this._size != 2) continue block29;
                                if (this._value != 35615L) {
                                    throw new ZipException("Invalid gzip bytes");
                                }
                                this._state = State.CM;
                                continue block29;
                            }
                            case CM: {
                                if ((currByte & 0xFF) != 8) {
                                    throw new ZipException("Invalid gzip compression method");
                                }
                                this._state = State.FLG;
                                continue block29;
                            }
                            case FLG: {
                                this._flags = currByte;
                                this._state = State.MTIME;
                                this._size = 0;
                                this._value = 0L;
                                continue block29;
                            }
                            case MTIME: {
                                ++this._size;
                                if (this._size != 4) continue block29;
                                this._state = State.XFL;
                                continue block29;
                            }
                            case XFL: {
                                this._state = State.OS;
                                continue block29;
                            }
                            case OS: {
                                this._state = State.FLAGS;
                                continue block29;
                            }
                            case EXTRA_LENGTH: {
                                this._value += ((long)currByte & 0xFFL) << 8 * this._size;
                                ++this._size;
                                if (this._size != 2) continue block29;
                                this._state = State.EXTRA;
                                continue block29;
                            }
                            case EXTRA: {
                                --this._value;
                                if (this._value != 0L) continue block29;
                                this._flags = (byte)(this._flags & 0xFFFFFFFB);
                                this._state = State.FLAGS;
                                continue block29;
                            }
                            case NAME: {
                                if (currByte != 0) continue block29;
                                this._flags = (byte)(this._flags & 0xFFFFFFF7);
                                this._state = State.FLAGS;
                                continue block29;
                            }
                            case COMMENT: {
                                if (currByte != 0) continue block29;
                                this._flags = (byte)(this._flags & 0xFFFFFFEF);
                                this._state = State.FLAGS;
                                continue block29;
                            }
                            case HCRC: {
                                ++this._size;
                                if (this._size != 2) continue block29;
                                this._flags = (byte)(this._flags & 0xFFFFFFFD);
                                this._state = State.FLAGS;
                                continue block29;
                            }
                            case CRC: {
                                this._value += ((long)currByte & 0xFFL) << 8 * this._size;
                                ++this._size;
                                if (this._size != 4) continue block29;
                                this._state = State.ISIZE;
                                this._size = 0;
                                this._value = 0L;
                                continue block29;
                            }
                            case ISIZE: {
                                this._value |= ((long)currByte & 0xFFL) << 8 * this._size;
                                ++this._size;
                                if (this._size != 4) continue block29;
                                if (this._value != (this._inflater.getBytesWritten() & 0xFFFFFFFFL)) {
                                    throw new ZipException("Invalid input size");
                                }
                                this.reset();
                                if (buffer == null) return;
                                this.release(buffer);
                                return;
                            }
                        }
                        throw new ZipException();
                    }
                    if (buffer == null) return;
                    this.release(buffer);
                    return;
                }
                while (true) {
                    if (buffer == null) {
                        buffer = this.acquire(this._bufferSize);
                    }
                    if (this._inflater.needsInput()) {
                        if (!compressed.hasRemaining()) {
                            if (buffer == null) return;
                            this.release(buffer);
                            return;
                        }
                        this._inflater.setInput(compressed);
                    }
                    try {
                        int pos = BufferUtil.flipToFill((ByteBuffer)buffer);
                        this._inflater.inflate(buffer);
                        BufferUtil.flipToFlush((ByteBuffer)buffer, (int)pos);
                    }
                    catch (DataFormatException x) {
                        throw new ZipException(x.getMessage());
                    }
                    if (buffer.hasRemaining()) {
                        ByteBuffer chunk = buffer;
                        buffer = null;
                        if (!this.decodedChunk(chunk)) continue;
                        if (buffer == null) return;
                        this.release(buffer);
                        return;
                    }
                    if (this._inflater.finished()) break;
                }
                this._state = State.CRC;
                this._size = 0;
                this._value = 0L;
            }
        }
        catch (ZipException x) {
            try {
                throw new RuntimeException(x);
            }
            catch (Throwable throwable) {
                if (buffer == null) throw throwable;
                this.release(buffer);
                throw throwable;
            }
        }
    }

    private void reset() {
        this._inflater.reset();
        this._state = State.INITIAL;
        this._size = 0;
        this._value = 0L;
        this._flags = 0;
    }

    public void destroy() {
        this._inflaterEntry.release();
        this._inflaterEntry = null;
        this._inflater = null;
    }

    public boolean isFinished() {
        return this._state == State.INITIAL;
    }

    public ByteBuffer acquire(int capacity) {
        return this._pool == null ? BufferUtil.allocate((int)capacity) : this._pool.acquire(capacity, this._useDirectBuffers);
    }

    public void release(ByteBuffer buffer) {
        if (this._pool != null && !BufferUtil.isTheEmptyBuffer((ByteBuffer)buffer)) {
            this._pool.release(buffer);
        }
    }

    private static enum State {
        INITIAL,
        ID,
        CM,
        FLG,
        MTIME,
        XFL,
        OS,
        FLAGS,
        EXTRA_LENGTH,
        EXTRA,
        NAME,
        COMMENT,
        HCRC,
        DATA,
        CRC,
        ISIZE;

    }
}

