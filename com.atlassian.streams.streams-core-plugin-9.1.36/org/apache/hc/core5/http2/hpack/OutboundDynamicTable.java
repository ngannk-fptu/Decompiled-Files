/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http2.hpack.FifoLinkedList;
import org.apache.hc.core5.http2.hpack.HPackEntry;
import org.apache.hc.core5.http2.hpack.HPackHeader;
import org.apache.hc.core5.http2.hpack.StaticTable;
import org.apache.hc.core5.util.Asserts;

final class OutboundDynamicTable {
    private final StaticTable staticTable;
    private final FifoLinkedList headers;
    private final Map<String, LinkedList<HPackEntry>> mapByName;
    private int maxSize;
    private int currentSize;

    OutboundDynamicTable(StaticTable staticTable) {
        this.staticTable = staticTable;
        this.headers = new FifoLinkedList();
        this.mapByName = new HashMap<String, LinkedList<HPackEntry>>();
        this.maxSize = Integer.MAX_VALUE;
        this.currentSize = 0;
    }

    OutboundDynamicTable() {
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

    public Header getHeader(int index) {
        if (index < 1 || index > this.length()) {
            throw new IndexOutOfBoundsException();
        }
        return index <= this.staticTable.length() ? this.staticTable.get(index) : this.headers.get(index - this.staticTable.length() - 1);
    }

    public void add(HPackHeader header) {
        int entrySize = header.getTotalSize();
        if (entrySize > this.maxSize) {
            this.clear();
            this.mapByName.clear();
            return;
        }
        String key = header.getName();
        FifoLinkedList.InternalNode node = this.headers.addFirst(header);
        LinkedList<HPackEntry> nodes = this.mapByName.get(key);
        if (nodes == null) {
            nodes = new LinkedList();
            this.mapByName.put(key, nodes);
        }
        nodes.addFirst(node);
        this.currentSize += entrySize;
        this.evict();
    }

    private void clear() {
        this.currentSize = 0;
        this.headers.clear();
    }

    public List<HPackEntry> getByName(String key) {
        return this.mapByName.get(key);
    }

    private void evict() {
        while (this.currentSize > this.maxSize) {
            FifoLinkedList.InternalNode node = this.headers.removeLast();
            if (node != null) {
                HPackHeader header = node.getHeader();
                this.currentSize -= header.getTotalSize();
                String key = header.getName();
                LinkedList<HPackEntry> nodes = this.mapByName.get(key);
                if (nodes == null) continue;
                nodes.remove(node);
                continue;
            }
            Asserts.check(this.currentSize == 0, "Current table size must be zero");
            break;
        }
    }
}

