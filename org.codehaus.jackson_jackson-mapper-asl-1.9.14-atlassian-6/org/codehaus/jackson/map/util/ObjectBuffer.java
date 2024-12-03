/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.lang.reflect.Array;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjectBuffer {
    static final int INITIAL_CHUNK_SIZE = 12;
    static final int SMALL_CHUNK_SIZE = 16384;
    static final int MAX_CHUNK_SIZE = 262144;
    private Node _bufferHead;
    private Node _bufferTail;
    private int _bufferedEntryCount;
    private Object[] _freeBuffer;

    public Object[] resetAndStart() {
        this._reset();
        if (this._freeBuffer == null) {
            return new Object[12];
        }
        return this._freeBuffer;
    }

    public Object[] appendCompletedChunk(Object[] fullChunk) {
        Node next = new Node(fullChunk);
        if (this._bufferHead == null) {
            this._bufferHead = this._bufferTail = next;
        } else {
            this._bufferTail.linkNext(next);
            this._bufferTail = next;
        }
        int len = fullChunk.length;
        this._bufferedEntryCount += len;
        len = len < 16384 ? (len += len) : (len += len >> 2);
        return new Object[len];
    }

    public Object[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries) {
        int totalSize = lastChunkEntries + this._bufferedEntryCount;
        Object[] result = new Object[totalSize];
        this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
        return result;
    }

    public <T> T[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, Class<T> componentType) {
        int totalSize = lastChunkEntries + this._bufferedEntryCount;
        Object[] result = (Object[])Array.newInstance(componentType, totalSize);
        this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
        this._reset();
        return result;
    }

    public void completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, List<Object> resultList) {
        for (Node n = this._bufferHead; n != null; n = n.next()) {
            Object[] curr = n.getData();
            int len = curr.length;
            for (int i = 0; i < len; ++i) {
                resultList.add(curr[i]);
            }
        }
        for (int i = 0; i < lastChunkEntries; ++i) {
            resultList.add(lastChunk[i]);
        }
    }

    public int initialCapacity() {
        return this._freeBuffer == null ? 0 : this._freeBuffer.length;
    }

    public int bufferedSize() {
        return this._bufferedEntryCount;
    }

    protected void _reset() {
        if (this._bufferTail != null) {
            this._freeBuffer = this._bufferTail.getData();
        }
        this._bufferTail = null;
        this._bufferHead = null;
        this._bufferedEntryCount = 0;
    }

    protected final void _copyTo(Object resultArray, int totalSize, Object[] lastChunk, int lastChunkEntries) {
        int ptr = 0;
        for (Node n = this._bufferHead; n != null; n = n.next()) {
            Object[] curr = n.getData();
            int len = curr.length;
            System.arraycopy(curr, 0, resultArray, ptr, len);
            ptr += len;
        }
        System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
        if ((ptr += lastChunkEntries) != totalSize) {
            throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr);
        }
    }

    static final class Node {
        final Object[] _data;
        Node _next;

        public Node(Object[] data) {
            this._data = data;
        }

        public Object[] getData() {
            return this._data;
        }

        public Node next() {
            return this._next;
        }

        public void linkNext(Node next) {
            if (this._next != null) {
                throw new IllegalStateException();
            }
            this._next = next;
        }
    }
}

