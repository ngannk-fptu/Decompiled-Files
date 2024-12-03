/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WeakMemoryHolder;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Memory
extends Pointer {
    private static ReferenceQueue<Memory> QUEUE = new ReferenceQueue();
    private static LinkedReference HEAD;
    private static final WeakMemoryHolder buffers;
    private final LinkedReference reference;
    protected long size;

    public static void purge() {
        buffers.clean();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void disposeAll() {
        Object object = LinkedReference.class;
        synchronized (LinkedReference.class) {
            LinkedReference entry;
            while ((entry = HEAD) != null) {
                Memory memory = (Memory)HEAD.get();
                if (memory != null) {
                    memory.dispose();
                } else {
                    Memory.HEAD.unlink();
                }
                if (HEAD != entry) continue;
                throw new IllegalStateException("the HEAD did not change");
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            object = QUEUE;
            synchronized (object) {
                LinkedReference stale;
                while ((stale = (LinkedReference)QUEUE.poll()) != null) {
                    stale.unlink();
                }
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int integrityCheck() {
        Class<LinkedReference> clazz = LinkedReference.class;
        synchronized (LinkedReference.class) {
            if (HEAD == null) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return 0;
            }
            ArrayList<LinkedReference> entries = new ArrayList<LinkedReference>();
            LinkedReference entry = HEAD;
            while (entry != null) {
                entries.add(entry);
                entry = entry.next;
            }
            int index = entries.size() - 1;
            entry = (LinkedReference)entries.get(index);
            while (entry != null) {
                if (entries.get(index) != entry) {
                    throw new IllegalStateException(entries.get(index) + " vs. " + entry + " at index " + index);
                }
                entry = entry.prev;
                --index;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return entries.size();
        }
    }

    public Memory(long size) {
        this.size = size;
        if (size <= 0L) {
            throw new IllegalArgumentException("Allocation size must be greater than zero");
        }
        this.peer = Memory.malloc(size);
        if (this.peer == 0L) {
            throw new OutOfMemoryError("Cannot allocate " + size + " bytes");
        }
        this.reference = LinkedReference.track(this);
    }

    protected Memory() {
        this.reference = null;
    }

    @Override
    public Pointer share(long offset) {
        return this.share(offset, this.size() - offset);
    }

    @Override
    public Pointer share(long offset, long sz) {
        this.boundsCheck(offset, sz);
        return new SharedMemory(offset, sz);
    }

    public Memory align(int byteBoundary) {
        if (byteBoundary <= 0) {
            throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
        }
        for (int i = 0; i < 32; ++i) {
            if (byteBoundary != 1 << i) continue;
            long mask = (long)byteBoundary - 1L ^ 0xFFFFFFFFFFFFFFFFL;
            if ((this.peer & mask) != this.peer) {
                long newPeer = this.peer + (long)byteBoundary - 1L & mask;
                long newSize = this.peer + this.size - newPeer;
                if (newSize <= 0L) {
                    throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
                }
                return (Memory)this.share(newPeer - this.peer, newSize);
            }
            return this;
        }
        throw new IllegalArgumentException("Byte boundary must be a power of two");
    }

    protected void finalize() {
        this.dispose();
    }

    protected synchronized void dispose() {
        if (this.peer == 0L) {
            return;
        }
        try {
            Memory.free(this.peer);
        }
        finally {
            this.peer = 0L;
            this.reference.unlink();
        }
    }

    public void clear() {
        this.clear(this.size);
    }

    public boolean valid() {
        return this.peer != 0L;
    }

    public long size() {
        return this.size;
    }

    protected void boundsCheck(long off, long sz) {
        if (off < 0L) {
            throw new IndexOutOfBoundsException("Invalid offset: " + off);
        }
        if (off + sz > this.size) {
            String msg = "Bounds exceeds available space : size=" + this.size + ", offset=" + (off + sz);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    @Override
    public void read(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 1L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, length * Native.WCHAR_SIZE);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void read(long bOff, Pointer[] buf, int index, int length) {
        this.boundsCheck(bOff, length * Native.POINTER_SIZE);
        super.read(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 1L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 2L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, length * Native.WCHAR_SIZE);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 4L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, (long)length * 8L);
        super.write(bOff, buf, index, length);
    }

    @Override
    public void write(long bOff, Pointer[] buf, int index, int length) {
        this.boundsCheck(bOff, length * Native.POINTER_SIZE);
        super.write(bOff, buf, index, length);
    }

    @Override
    public byte getByte(long offset) {
        this.boundsCheck(offset, 1L);
        return super.getByte(offset);
    }

    @Override
    public char getChar(long offset) {
        this.boundsCheck(offset, Native.WCHAR_SIZE);
        return super.getChar(offset);
    }

    @Override
    public short getShort(long offset) {
        this.boundsCheck(offset, 2L);
        return super.getShort(offset);
    }

    @Override
    public int getInt(long offset) {
        this.boundsCheck(offset, 4L);
        return super.getInt(offset);
    }

    @Override
    public long getLong(long offset) {
        this.boundsCheck(offset, 8L);
        return super.getLong(offset);
    }

    @Override
    public float getFloat(long offset) {
        this.boundsCheck(offset, 4L);
        return super.getFloat(offset);
    }

    @Override
    public double getDouble(long offset) {
        this.boundsCheck(offset, 8L);
        return super.getDouble(offset);
    }

    @Override
    public Pointer getPointer(long offset) {
        this.boundsCheck(offset, Native.POINTER_SIZE);
        return this.shareReferenceIfInBounds(super.getPointer(offset));
    }

    @Override
    public ByteBuffer getByteBuffer(long offset, long length) {
        this.boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        buffers.put(b, this);
        return b;
    }

    @Override
    public String getString(long offset, String encoding) {
        this.boundsCheck(offset, 0L);
        return super.getString(offset, encoding);
    }

    @Override
    public String getWideString(long offset) {
        this.boundsCheck(offset, 0L);
        return super.getWideString(offset);
    }

    @Override
    public void setByte(long offset, byte value) {
        this.boundsCheck(offset, 1L);
        super.setByte(offset, value);
    }

    @Override
    public void setChar(long offset, char value) {
        this.boundsCheck(offset, Native.WCHAR_SIZE);
        super.setChar(offset, value);
    }

    @Override
    public void setShort(long offset, short value) {
        this.boundsCheck(offset, 2L);
        super.setShort(offset, value);
    }

    @Override
    public void setInt(long offset, int value) {
        this.boundsCheck(offset, 4L);
        super.setInt(offset, value);
    }

    @Override
    public void setLong(long offset, long value) {
        this.boundsCheck(offset, 8L);
        super.setLong(offset, value);
    }

    @Override
    public void setFloat(long offset, float value) {
        this.boundsCheck(offset, 4L);
        super.setFloat(offset, value);
    }

    @Override
    public void setDouble(long offset, double value) {
        this.boundsCheck(offset, 8L);
        super.setDouble(offset, value);
    }

    @Override
    public void setPointer(long offset, Pointer value) {
        this.boundsCheck(offset, Native.POINTER_SIZE);
        super.setPointer(offset, value);
    }

    @Override
    public void setString(long offset, String value, String encoding) {
        this.boundsCheck(offset, (long)Native.getBytes(value, encoding).length + 1L);
        super.setString(offset, value, encoding);
    }

    @Override
    public void setWideString(long offset, String value) {
        this.boundsCheck(offset, ((long)value.length() + 1L) * (long)Native.WCHAR_SIZE);
        super.setWideString(offset, value);
    }

    @Override
    public String toString() {
        return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
    }

    protected static void free(long p) {
        if (p != 0L) {
            Native.free(p);
        }
    }

    protected static long malloc(long size) {
        return Native.malloc(size);
    }

    public String dump() {
        return this.dump(0L, (int)this.size());
    }

    private Pointer shareReferenceIfInBounds(Pointer target) {
        if (target == null) {
            return null;
        }
        long offset = target.peer - this.peer;
        if (offset >= 0L && offset < this.size) {
            return this.share(offset);
        }
        return target;
    }

    static {
        buffers = new WeakMemoryHolder();
    }

    private class SharedMemory
    extends Memory {
        public SharedMemory(long offset, long size) {
            this.size = size;
            this.peer = Memory.this.peer + offset;
        }

        @Override
        protected synchronized void dispose() {
            this.peer = 0L;
        }

        @Override
        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
        }

        @Override
        public String toString() {
            return super.toString() + " (shared from " + Memory.this.toString() + ")";
        }
    }

    private static class LinkedReference
    extends WeakReference<Memory> {
        private LinkedReference next;
        private LinkedReference prev;

        private LinkedReference(Memory referent) {
            super(referent, QUEUE);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        static LinkedReference track(Memory instance) {
            ReferenceQueue referenceQueue = QUEUE;
            synchronized (referenceQueue) {
                LinkedReference stale;
                while ((stale = (LinkedReference)QUEUE.poll()) != null) {
                    stale.unlink();
                }
            }
            LinkedReference entry = new LinkedReference(instance);
            Class<LinkedReference> clazz = LinkedReference.class;
            synchronized (LinkedReference.class) {
                if (HEAD != null) {
                    entry.next = HEAD;
                    HEAD.prev = entry;
                    HEAD = HEAD.prev;
                } else {
                    HEAD = entry;
                }
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return entry;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void unlink() {
            Class<LinkedReference> clazz = LinkedReference.class;
            synchronized (LinkedReference.class) {
                LinkedReference next;
                if (HEAD != this) {
                    if (this.prev == null) {
                        // ** MonitorExit[var1_1] (shouldn't be in output)
                        return;
                    }
                    next = this.prev.next = this.next;
                } else {
                    next = HEAD = HEAD.next;
                }
                if (next != null) {
                    next.prev = this.prev;
                }
                this.prev = null;
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        }
    }
}

