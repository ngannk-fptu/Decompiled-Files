/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http2.hpack.HPackHeader;

final class FifoBuffer {
    private HPackHeader[] array;
    private int head;
    private int tail;

    FifoBuffer(int initialCapacity) {
        this.array = new HPackHeader[initialCapacity];
        this.head = 0;
        this.tail = 0;
    }

    private void expand() {
        int newcapacity = this.array.length + 1 << 1;
        if (newcapacity < 0) {
            newcapacity = Integer.MAX_VALUE;
        }
        HPackHeader[] oldArray = this.array;
        int len = oldArray.length;
        HPackHeader[] newArray = new HPackHeader[newcapacity];
        System.arraycopy(oldArray, this.head, newArray, 0, len - this.head);
        System.arraycopy(oldArray, 0, newArray, len - this.head, this.head);
        this.array = newArray;
        this.head = len;
        this.tail = 0;
    }

    public void clear() {
        this.head = 0;
        this.tail = 0;
    }

    public void addFirst(HPackHeader header) {
        this.array[this.head++] = header;
        if (this.head == this.array.length) {
            this.head = 0;
        }
        if (this.head == this.tail) {
            this.expand();
        }
    }

    public HPackHeader get(int index) {
        int i = this.head - index - 1;
        if (i < 0) {
            i = this.array.length + i;
        }
        return this.array[i];
    }

    public HPackHeader getFirst() {
        return this.array[this.head > 0 ? this.head - 1 : this.array.length - 1];
    }

    public HPackHeader getLast() {
        return this.array[this.tail];
    }

    public HPackHeader removeLast() {
        HPackHeader header = this.array[this.tail];
        if (header != null) {
            this.array[this.tail++] = null;
            if (this.tail == this.array.length) {
                this.tail = 0;
            }
        }
        return header;
    }

    public int capacity() {
        return this.array.length;
    }

    public int size() {
        int i = this.head - this.tail;
        if (i < 0) {
            i = this.array.length + i;
        }
        return i;
    }
}

