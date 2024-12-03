/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.hc.core5.http2.hpack.HPackEntry;
import org.apache.hc.core5.http2.hpack.HPackHeader;

final class StaticTable {
    static final HPackHeader[] STANDARD_HEADERS = new HPackHeader[]{new HPackHeader(":authority", ""), new HPackHeader(":method", "GET"), new HPackHeader(":method", "POST"), new HPackHeader(":path", "/"), new HPackHeader(":path", "/index.html"), new HPackHeader(":scheme", "http"), new HPackHeader(":scheme", "https"), new HPackHeader(":status", "200"), new HPackHeader(":status", "204"), new HPackHeader(":status", "206"), new HPackHeader(":status", "304"), new HPackHeader(":status", "400"), new HPackHeader(":status", "404"), new HPackHeader(":status", "500"), new HPackHeader("accept-charset", ""), new HPackHeader("accept-encoding", "gzip, deflate"), new HPackHeader("accept-language", ""), new HPackHeader("accept-ranges", ""), new HPackHeader("accept", ""), new HPackHeader("access-control-allow-origin", ""), new HPackHeader("age", ""), new HPackHeader("allow", ""), new HPackHeader("authorization", ""), new HPackHeader("cache-control", ""), new HPackHeader("content-disposition", ""), new HPackHeader("content-encoding", ""), new HPackHeader("content-language", ""), new HPackHeader("content-length", ""), new HPackHeader("content-location", ""), new HPackHeader("content-range", ""), new HPackHeader("content-type", ""), new HPackHeader("cookie", ""), new HPackHeader("date", ""), new HPackHeader("etag", ""), new HPackHeader("expect", ""), new HPackHeader("expires", ""), new HPackHeader("from", ""), new HPackHeader("host", ""), new HPackHeader("if-match", ""), new HPackHeader("if-modified-since", ""), new HPackHeader("if-none-match", ""), new HPackHeader("if-range", ""), new HPackHeader("if-unmodified-since", ""), new HPackHeader("last-modified", ""), new HPackHeader("link", ""), new HPackHeader("location", ""), new HPackHeader("max-forwards", ""), new HPackHeader("proxy-authenticate", ""), new HPackHeader("proxy-authorization", ""), new HPackHeader("range", ""), new HPackHeader("referer", ""), new HPackHeader("refresh", ""), new HPackHeader("retry-after", ""), new HPackHeader("server", ""), new HPackHeader("set-cookie", ""), new HPackHeader("strict-transport-security", ""), new HPackHeader("transfer-encoding", ""), new HPackHeader("user-agent", ""), new HPackHeader("vary", ""), new HPackHeader("via", ""), new HPackHeader("www-authenticate", "")};
    static final StaticTable INSTANCE = new StaticTable(STANDARD_HEADERS);
    private final HPackHeader[] headers;
    private final ConcurrentMap<String, CopyOnWriteArrayList<HPackEntry>> mapByName;

    StaticTable(HPackHeader ... headers) {
        this.headers = headers;
        this.mapByName = new ConcurrentHashMap<String, CopyOnWriteArrayList<HPackEntry>>();
        for (int i = 0; i < headers.length; ++i) {
            HPackHeader header = headers[i];
            String key = header.getName();
            CopyOnWriteArrayList<HPackEntry> entries = (CopyOnWriteArrayList<HPackEntry>)this.mapByName.get(key);
            if (entries == null) {
                entries = new CopyOnWriteArrayList<HPackEntry>(new HPackEntry[]{new InternalEntry(header, i)});
                this.mapByName.put(key, entries);
                continue;
            }
            entries.add(new InternalEntry(header, i));
        }
    }

    public int length() {
        return this.headers.length;
    }

    public HPackHeader get(int index) {
        return this.headers[index - 1];
    }

    public List<HPackEntry> getByName(String key) {
        return (List)this.mapByName.get(key);
    }

    static class InternalEntry
    implements HPackEntry {
        private final HPackHeader header;
        private final int index;

        InternalEntry(HPackHeader header, int index) {
            this.header = header;
            this.index = index;
        }

        @Override
        public int getIndex() {
            return this.index + 1;
        }

        @Override
        public HPackHeader getHeader() {
            return this.header;
        }
    }
}

