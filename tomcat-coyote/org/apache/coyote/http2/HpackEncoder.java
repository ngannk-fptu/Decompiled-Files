/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http2.HPackHuffman;
import org.apache.coyote.http2.Hpack;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.res.StringManager;

class HpackEncoder {
    private static final Log log = LogFactory.getLog(HpackEncoder.class);
    private static final StringManager sm = StringManager.getManager(HpackEncoder.class);
    private static final HpackHeaderFunction DEFAULT_HEADER_FUNCTION = new HpackHeaderFunction(){

        @Override
        public boolean shouldUseIndexing(String headerName, String value) {
            switch (headerName) {
                case "content-length": 
                case "date": {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean shouldUseHuffman(String header, String value) {
            return value.length() > 5;
        }

        @Override
        public boolean shouldUseHuffman(String header) {
            return header.length() > 5;
        }
    };
    private int headersIterator = -1;
    private boolean firstPass = true;
    private MimeHeaders currentHeaders;
    private int entryPositionCounter;
    private int newMaxHeaderSize = -1;
    private int minNewMaxHeaderSize = -1;
    private static final Map<String, TableEntry[]> ENCODING_STATIC_TABLE;
    private final Deque<TableEntry> evictionQueue = new ArrayDeque<TableEntry>();
    private final Map<String, List<TableEntry>> dynamicTable = new HashMap<String, List<TableEntry>>();
    private int maxTableSize = 4096;
    private int currentTableSize;
    private final HpackHeaderFunction hpackHeaderFunction = DEFAULT_HEADER_FUNCTION;

    HpackEncoder() {
    }

    State encode(MimeHeaders headers, ByteBuffer target) {
        int it = this.headersIterator;
        if (this.headersIterator == -1) {
            this.handleTableSizeChange(target);
            it = 0;
            this.currentHeaders = headers;
        } else if (headers != this.currentHeaders) {
            throw new IllegalStateException();
        }
        while (it < this.currentHeaders.size()) {
            String headerName = headers.getName(it).toString().toLowerCase(Locale.US);
            boolean skip = false;
            if (this.firstPass) {
                if (headerName.charAt(0) != ':') {
                    skip = true;
                }
            } else if (headerName.charAt(0) == ':') {
                skip = true;
            }
            if (!skip) {
                boolean canIndex;
                String val = headers.getValue(it).toString();
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("hpackEncoder.encodeHeader", new Object[]{headerName, val}));
                }
                TableEntry tableEntry = this.findInTable(headerName, val);
                int required = 11 + headerName.length() + 1 + val.length();
                if (target.remaining() < required) {
                    this.headersIterator = it;
                    return State.UNDERFLOW;
                }
                boolean bl = canIndex = this.hpackHeaderFunction.shouldUseIndexing(headerName, val) && headerName.length() + val.length() + 32 < this.maxTableSize;
                if (tableEntry == null && canIndex) {
                    target.put((byte)64);
                    this.writeHuffmanEncodableName(target, headerName);
                    this.writeHuffmanEncodableValue(target, headerName, val);
                    this.addToDynamicTable(headerName, val);
                } else if (tableEntry == null) {
                    target.put((byte)16);
                    this.writeHuffmanEncodableName(target, headerName);
                    this.writeHuffmanEncodableValue(target, headerName, val);
                } else if (val.equals(tableEntry.value)) {
                    target.put((byte)-128);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 7);
                } else if (canIndex) {
                    target.put((byte)64);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 6);
                    this.writeHuffmanEncodableValue(target, headerName, val);
                    this.addToDynamicTable(headerName, val);
                } else {
                    target.put((byte)16);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 4);
                    this.writeHuffmanEncodableValue(target, headerName, val);
                }
            }
            if (++it != this.currentHeaders.size() || !this.firstPass) continue;
            this.firstPass = false;
            it = 0;
        }
        this.headersIterator = -1;
        this.firstPass = true;
        return State.COMPLETE;
    }

    private void writeHuffmanEncodableName(ByteBuffer target, String headerName) {
        if (this.hpackHeaderFunction.shouldUseHuffman(headerName) && HPackHuffman.encode(target, headerName, true)) {
            return;
        }
        target.put((byte)0);
        Hpack.encodeInteger(target, headerName.length(), 7);
        for (int j = 0; j < headerName.length(); ++j) {
            target.put((byte)Hpack.toLower(headerName.charAt(j)));
        }
    }

    private void writeHuffmanEncodableValue(ByteBuffer target, String headerName, String val) {
        if (this.hpackHeaderFunction.shouldUseHuffman(headerName, val)) {
            if (!HPackHuffman.encode(target, val, false)) {
                this.writeValueString(target, val);
            }
        } else {
            this.writeValueString(target, val);
        }
    }

    private void writeValueString(ByteBuffer target, String val) {
        target.put((byte)0);
        Hpack.encodeInteger(target, val.length(), 7);
        for (int j = 0; j < val.length(); ++j) {
            target.put((byte)val.charAt(j));
        }
    }

    private void addToDynamicTable(String headerName, String val) {
        int pos = this.entryPositionCounter++;
        DynamicTableEntry d = new DynamicTableEntry(headerName, val, -pos);
        this.dynamicTable.computeIfAbsent(headerName, k -> new ArrayList(1)).add(d);
        this.evictionQueue.add(d);
        this.currentTableSize += d.getSize();
        this.runEvictionIfRequired();
        if (this.entryPositionCounter == Integer.MAX_VALUE) {
            this.preventPositionRollover();
        }
    }

    private void preventPositionRollover() {
        for (List<TableEntry> tableEntries : this.dynamicTable.values()) {
            for (TableEntry t : tableEntries) {
                t.position = t.getPosition();
            }
        }
        this.entryPositionCounter = 0;
    }

    private void runEvictionIfRequired() {
        while (this.currentTableSize > this.maxTableSize) {
            TableEntry next = this.evictionQueue.poll();
            if (next == null) {
                return;
            }
            this.currentTableSize -= next.size;
            List<TableEntry> list = this.dynamicTable.get(next.name);
            list.remove(next);
            if (!list.isEmpty()) continue;
            this.dynamicTable.remove(next.name);
        }
    }

    private TableEntry findInTable(String headerName, String value) {
        List<TableEntry> dynamic;
        TableEntry[] staticTable = ENCODING_STATIC_TABLE.get(headerName);
        if (staticTable != null) {
            for (TableEntry st : staticTable) {
                if (st.value == null || !st.value.equals(value)) continue;
                return st;
            }
        }
        if ((dynamic = this.dynamicTable.get(headerName)) != null) {
            for (TableEntry st : dynamic) {
                if (!st.value.equals(value)) continue;
                return st;
            }
        }
        if (staticTable != null) {
            return staticTable[0];
        }
        return null;
    }

    public void setMaxTableSize(int newSize) {
        this.newMaxHeaderSize = newSize;
        this.minNewMaxHeaderSize = this.minNewMaxHeaderSize == -1 ? newSize : Math.min(newSize, this.minNewMaxHeaderSize);
    }

    private void handleTableSizeChange(ByteBuffer target) {
        if (this.newMaxHeaderSize == -1) {
            return;
        }
        if (this.minNewMaxHeaderSize != this.newMaxHeaderSize) {
            target.put((byte)32);
            Hpack.encodeInteger(target, this.minNewMaxHeaderSize, 5);
        }
        target.put((byte)32);
        Hpack.encodeInteger(target, this.newMaxHeaderSize, 5);
        this.maxTableSize = this.newMaxHeaderSize;
        this.runEvictionIfRequired();
        this.newMaxHeaderSize = -1;
        this.minNewMaxHeaderSize = -1;
    }

    static {
        HashMap<String, TableEntry[]> map = new HashMap<String, TableEntry[]>();
        for (int i = 1; i < Hpack.STATIC_TABLE.length; ++i) {
            Hpack.HeaderField m = Hpack.STATIC_TABLE[i];
            TableEntry[] existing = (TableEntry[])map.get(m.name);
            if (existing == null) {
                map.put(m.name, new TableEntry[]{new TableEntry(m.name, m.value, i)});
                continue;
            }
            TableEntry[] newEntry = new TableEntry[existing.length + 1];
            System.arraycopy(existing, 0, newEntry, 0, existing.length);
            newEntry[existing.length] = new TableEntry(m.name, m.value, i);
            map.put(m.name, newEntry);
        }
        ENCODING_STATIC_TABLE = Collections.unmodifiableMap(map);
    }

    private static interface HpackHeaderFunction {
        public boolean shouldUseIndexing(String var1, String var2);

        public boolean shouldUseHuffman(String var1, String var2);

        public boolean shouldUseHuffman(String var1);
    }

    private static class TableEntry {
        private final String name;
        private final String value;
        private final int size;
        private int position;

        private TableEntry(String name, String value, int position) {
            this.name = name;
            this.value = value;
            this.position = position;
            this.size = value != null ? 32 + name.length() + value.length() : -1;
        }

        int getPosition() {
            return this.position;
        }

        int getSize() {
            return this.size;
        }
    }

    static enum State {
        COMPLETE,
        UNDERFLOW;

    }

    private class DynamicTableEntry
    extends TableEntry {
        private DynamicTableEntry(String name, String value, int position) {
            super(name, value, position);
        }

        @Override
        int getPosition() {
            return super.getPosition() + HpackEncoder.this.entryPositionCounter + Hpack.STATIC_TABLE_LENGTH;
        }
    }
}

