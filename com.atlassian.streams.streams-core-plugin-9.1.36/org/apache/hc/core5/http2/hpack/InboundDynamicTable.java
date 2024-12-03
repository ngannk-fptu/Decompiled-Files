/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http2.hpack.FifoBuffer;
import org.apache.hc.core5.http2.hpack.HPackHeader;
import org.apache.hc.core5.http2.hpack.StaticTable;
import org.apache.hc.core5.util.Asserts;

final class InboundDynamicTable {
    private final StaticTable staticTable;
    private final FifoBuffer headers;
    private int maxSize;
    private int currentSize;

    InboundDynamicTable(StaticTable staticTable) {
        this.staticTable = staticTable;
        this.headers = new FifoBuffer(256);
        this.maxSize = Integer.MAX_VALUE;
        this.currentSize = 0;
    }

    InboundDynamicTable() {
        this(StaticTable.INSTANCE);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        this.evict();
    }

    public int getCurrentSize() {
        return this.currentSize;
    }

    int staticLength() {
        return this.staticTable.length();
    }

    int dynamicLength() {
        return this.headers.size();
    }

    Header getDynamicEntry(int index) {
        return this.headers.get(index);
    }

    public int length() {
        return this.staticTable.length() + this.headers.size();
    }

    public HPackHeader getHeader(int index) {
        if (index < 1 || index > this.length()) {
            throw new IndexOutOfBoundsException();
        }
        return index <= this.staticTable.length() ? this.staticTable.get(index) : this.headers.get(index - this.staticTable.length() - 1);
    }

    public void add(HPackHeader header) {
        int entrySize = header.getTotalSize();
        if (entrySize > this.maxSize) {
            this.clear();
            return;
        }
        this.headers.addFirst(header);
        this.currentSize += entrySize;
        this.evict();
    }

    private void clear() {
        this.currentSize = 0;
        this.headers.clear();
    }

    private void evict() {
        while (this.currentSize > this.maxSize) {
            HPackHeader header = this.headers.removeLast();
            if (header != null) {
                this.currentSize -= header.getTotalSize();
                continue;
            }
            Asserts.check(this.currentSize == 0, "Current table size must be zero");
            break;
        }
    }
}

