/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;

public class ByteBufferCallbackAccumulator {
    private final List<Entry> _entries = new ArrayList<Entry>();
    private int _length;

    public void addEntry(ByteBuffer buffer, Callback callback) {
        this._entries.add(new Entry(buffer, callback));
        this._length = Math.addExact(this._length, buffer.remaining());
    }

    public int getLength() {
        return this._length;
    }

    public byte[] takeByteArray() {
        int length = this.getLength();
        if (length == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[length];
        ByteBuffer buffer = BufferUtil.toBuffer((byte[])bytes);
        BufferUtil.clear((ByteBuffer)buffer);
        this.writeTo(buffer);
        return bytes;
    }

    public void writeTo(ByteBuffer buffer) {
        if (BufferUtil.space((ByteBuffer)buffer) < this._length) {
            throw new IllegalArgumentException("not enough buffer space remaining");
        }
        int pos = BufferUtil.flipToFill((ByteBuffer)buffer);
        for (Entry entry : this._entries) {
            buffer.put(entry.buffer);
            entry.callback.succeeded();
        }
        BufferUtil.flipToFlush((ByteBuffer)buffer, (int)pos);
        this._entries.clear();
        this._length = 0;
    }

    public void fail(Throwable t) {
        ArrayList<Entry> entries = new ArrayList<Entry>(this._entries);
        this._entries.clear();
        this._length = 0;
        for (Entry entry : entries) {
            entry.callback.failed(t);
        }
    }

    private static class Entry {
        private final ByteBuffer buffer;
        private final Callback callback;

        Entry(ByteBuffer buffer, Callback callback) {
            this.buffer = buffer;
            this.callback = callback;
        }
    }
}

