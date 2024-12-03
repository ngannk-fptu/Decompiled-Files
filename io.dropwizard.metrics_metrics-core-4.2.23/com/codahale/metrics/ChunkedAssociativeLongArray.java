/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

class ChunkedAssociativeLongArray {
    private static final long[] EMPTY = new long[0];
    private static final int DEFAULT_CHUNK_SIZE = 512;
    private static final int MAX_CACHE_SIZE = 128;
    private final int defaultChunkSize;
    private final ArrayDeque<SoftReference<Chunk>> chunksCache = new ArrayDeque();
    private final Deque<Chunk> chunks = new ArrayDeque<Chunk>();

    ChunkedAssociativeLongArray() {
        this(512);
    }

    ChunkedAssociativeLongArray(int chunkSize) {
        this.defaultChunkSize = chunkSize;
    }

    private Chunk allocateChunk() {
        SoftReference<Chunk> chunkRef;
        Chunk chunk;
        do {
            if ((chunkRef = this.chunksCache.pollLast()) != null) continue;
            return new Chunk(this.defaultChunkSize);
        } while ((chunk = chunkRef.get()) == null);
        chunk.cursor = 0;
        chunk.startIndex = 0;
        chunk.chunkSize = chunk.keys.length;
        return chunk;
    }

    private void freeChunk(Chunk chunk) {
        if (this.chunksCache.size() < 128) {
            this.chunksCache.add(new SoftReference<Chunk>(chunk));
        }
    }

    synchronized boolean put(long key, long value) {
        Chunk activeChunk = this.chunks.peekLast();
        if (activeChunk != null && activeChunk.cursor != 0 && activeChunk.keys[activeChunk.cursor - 1] > key) {
            return false;
        }
        if (activeChunk == null || activeChunk.cursor - activeChunk.startIndex == activeChunk.chunkSize) {
            activeChunk = this.allocateChunk();
            this.chunks.add(activeChunk);
        }
        activeChunk.append(key, value);
        return true;
    }

    synchronized long[] values() {
        int valuesSize = this.size();
        if (valuesSize == 0) {
            return EMPTY;
        }
        long[] values = new long[valuesSize];
        int valuesIndex = 0;
        for (Chunk chunk : this.chunks) {
            int length = chunk.cursor - chunk.startIndex;
            int itemsToCopy = Math.min(valuesSize - valuesIndex, length);
            System.arraycopy(chunk.values, chunk.startIndex, values, valuesIndex, itemsToCopy);
            valuesIndex += length;
        }
        return values;
    }

    synchronized int size() {
        int result = 0;
        for (Chunk chunk : this.chunks) {
            result += chunk.cursor - chunk.startIndex;
        }
        return result;
    }

    synchronized String out() {
        StringBuilder builder = new StringBuilder();
        Iterator<Chunk> iterator = this.chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            builder.append('[');
            for (int i = chunk.startIndex; i < chunk.cursor; ++i) {
                builder.append('(').append(chunk.keys[i]).append(": ").append(chunk.values[i]).append(')').append(' ');
            }
            builder.append(']');
            if (!iterator.hasNext()) continue;
            builder.append("->");
        }
        return builder.toString();
    }

    synchronized void trim(long startKey, long endKey) {
        Iterator<Chunk> descendingIterator = this.chunks.descendingIterator();
        while (descendingIterator.hasNext()) {
            Chunk currentTail = descendingIterator.next();
            if (this.isFirstElementIsEmptyOrGreaterEqualThanKey(currentTail, endKey)) {
                this.freeChunk(currentTail);
                descendingIterator.remove();
                continue;
            }
            currentTail.cursor = this.findFirstIndexOfGreaterEqualElements(currentTail.keys, currentTail.startIndex, currentTail.cursor, endKey);
            break;
        }
        Iterator<Chunk> iterator = this.chunks.iterator();
        while (iterator.hasNext()) {
            Chunk currentHead = iterator.next();
            if (this.isLastElementIsLessThanKey(currentHead, startKey)) {
                this.freeChunk(currentHead);
                iterator.remove();
                continue;
            }
            int newStartIndex = this.findFirstIndexOfGreaterEqualElements(currentHead.keys, currentHead.startIndex, currentHead.cursor, startKey);
            if (currentHead.startIndex == newStartIndex) break;
            currentHead.startIndex = newStartIndex;
            currentHead.chunkSize = currentHead.cursor - currentHead.startIndex;
            break;
        }
    }

    synchronized void clear() {
        this.chunks.clear();
    }

    private boolean isFirstElementIsEmptyOrGreaterEqualThanKey(Chunk chunk, long key) {
        return chunk.cursor == chunk.startIndex || chunk.keys[chunk.startIndex] >= key;
    }

    private boolean isLastElementIsLessThanKey(Chunk chunk, long key) {
        return chunk.cursor == chunk.startIndex || chunk.keys[chunk.cursor - 1] < key;
    }

    private int findFirstIndexOfGreaterEqualElements(long[] array, int startIndex, int endIndex, long minKey) {
        if (endIndex == startIndex || array[startIndex] >= minKey) {
            return startIndex;
        }
        int keyIndex = Arrays.binarySearch(array, startIndex, endIndex, minKey);
        return keyIndex < 0 ? -(keyIndex + 1) : keyIndex;
    }

    private static class Chunk {
        private final long[] keys;
        private final long[] values;
        private int chunkSize;
        private int startIndex = 0;
        private int cursor = 0;

        private Chunk(int chunkSize) {
            this.chunkSize = chunkSize;
            this.keys = new long[chunkSize];
            this.values = new long[chunkSize];
        }

        private void append(long key, long value) {
            this.keys[this.cursor] = key;
            this.values[this.cursor] = value;
            ++this.cursor;
        }
    }
}

