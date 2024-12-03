/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.HpackHeaderField;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.util.AsciiString;
import io.netty.util.internal.PlatformDependent;
import java.util.Arrays;
import java.util.List;

final class HpackStaticTable {
    static final int NOT_FOUND = -1;
    private static final List<HpackHeaderField> STATIC_TABLE;
    private static final int HEADER_NAMES_TABLE_SIZE = 512;
    private static final int HEADER_NAMES_TABLE_SHIFT;
    private static final HeaderNameIndex[] HEADER_NAMES;
    private static final int HEADERS_WITH_NON_EMPTY_VALUES_TABLE_SIZE = 64;
    private static final int HEADERS_WITH_NON_EMPTY_VALUES_TABLE_SHIFT;
    private static final HeaderIndex[] HEADERS_WITH_NON_EMPTY_VALUES;
    static final int length;

    private static HpackHeaderField newEmptyHeaderField(String name) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.EMPTY_STRING);
    }

    private static HpackHeaderField newHeaderField(String name, String value) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.cached(value));
    }

    static HpackHeaderField getEntry(int index) {
        return STATIC_TABLE.get(index - 1);
    }

    static int getIndex(CharSequence name) {
        HeaderNameIndex entry = HpackStaticTable.getEntry(name);
        return entry == null ? -1 : entry.index;
    }

    static int getIndexInsensitive(CharSequence name, CharSequence value) {
        if (value.length() == 0) {
            HeaderNameIndex entry = HpackStaticTable.getEntry(name);
            return entry == null || !entry.emptyValue ? -1 : entry.index;
        }
        int bucket = HpackStaticTable.headerBucket(value);
        HeaderIndex header = HEADERS_WITH_NON_EMPTY_VALUES[bucket];
        if (header == null) {
            return -1;
        }
        if (HpackUtil.equalsVariableTime(header.name, name) && HpackUtil.equalsVariableTime(header.value, value)) {
            return header.index;
        }
        return -1;
    }

    private static HeaderNameIndex getEntry(CharSequence name) {
        int bucket = HpackStaticTable.headerNameBucket(name);
        HeaderNameIndex entry = HEADER_NAMES[bucket];
        if (entry == null) {
            return null;
        }
        return HpackUtil.equalsVariableTime(entry.name, name) ? entry : null;
    }

    private static int headerNameBucket(CharSequence name) {
        return HpackStaticTable.bucket(name, HEADER_NAMES_TABLE_SHIFT, 511);
    }

    private static int headerBucket(CharSequence value) {
        return HpackStaticTable.bucket(value, HEADERS_WITH_NON_EMPTY_VALUES_TABLE_SHIFT, 63);
    }

    private static int bucket(CharSequence s, int shift, int mask) {
        return AsciiString.hashCode(s) >> shift & mask;
    }

    private HpackStaticTable() {
    }

    static {
        Object tableEntry;
        int bucket;
        HpackHeaderField entry;
        int index;
        STATIC_TABLE = Arrays.asList(HpackStaticTable.newEmptyHeaderField(":authority"), HpackStaticTable.newHeaderField(":method", "GET"), HpackStaticTable.newHeaderField(":method", "POST"), HpackStaticTable.newHeaderField(":path", "/"), HpackStaticTable.newHeaderField(":path", "/index.html"), HpackStaticTable.newHeaderField(":scheme", "http"), HpackStaticTable.newHeaderField(":scheme", "https"), HpackStaticTable.newHeaderField(":status", "200"), HpackStaticTable.newHeaderField(":status", "204"), HpackStaticTable.newHeaderField(":status", "206"), HpackStaticTable.newHeaderField(":status", "304"), HpackStaticTable.newHeaderField(":status", "400"), HpackStaticTable.newHeaderField(":status", "404"), HpackStaticTable.newHeaderField(":status", "500"), HpackStaticTable.newEmptyHeaderField("accept-charset"), HpackStaticTable.newHeaderField("accept-encoding", "gzip, deflate"), HpackStaticTable.newEmptyHeaderField("accept-language"), HpackStaticTable.newEmptyHeaderField("accept-ranges"), HpackStaticTable.newEmptyHeaderField("accept"), HpackStaticTable.newEmptyHeaderField("access-control-allow-origin"), HpackStaticTable.newEmptyHeaderField("age"), HpackStaticTable.newEmptyHeaderField("allow"), HpackStaticTable.newEmptyHeaderField("authorization"), HpackStaticTable.newEmptyHeaderField("cache-control"), HpackStaticTable.newEmptyHeaderField("content-disposition"), HpackStaticTable.newEmptyHeaderField("content-encoding"), HpackStaticTable.newEmptyHeaderField("content-language"), HpackStaticTable.newEmptyHeaderField("content-length"), HpackStaticTable.newEmptyHeaderField("content-location"), HpackStaticTable.newEmptyHeaderField("content-range"), HpackStaticTable.newEmptyHeaderField("content-type"), HpackStaticTable.newEmptyHeaderField("cookie"), HpackStaticTable.newEmptyHeaderField("date"), HpackStaticTable.newEmptyHeaderField("etag"), HpackStaticTable.newEmptyHeaderField("expect"), HpackStaticTable.newEmptyHeaderField("expires"), HpackStaticTable.newEmptyHeaderField("from"), HpackStaticTable.newEmptyHeaderField("host"), HpackStaticTable.newEmptyHeaderField("if-match"), HpackStaticTable.newEmptyHeaderField("if-modified-since"), HpackStaticTable.newEmptyHeaderField("if-none-match"), HpackStaticTable.newEmptyHeaderField("if-range"), HpackStaticTable.newEmptyHeaderField("if-unmodified-since"), HpackStaticTable.newEmptyHeaderField("last-modified"), HpackStaticTable.newEmptyHeaderField("link"), HpackStaticTable.newEmptyHeaderField("location"), HpackStaticTable.newEmptyHeaderField("max-forwards"), HpackStaticTable.newEmptyHeaderField("proxy-authenticate"), HpackStaticTable.newEmptyHeaderField("proxy-authorization"), HpackStaticTable.newEmptyHeaderField("range"), HpackStaticTable.newEmptyHeaderField("referer"), HpackStaticTable.newEmptyHeaderField("refresh"), HpackStaticTable.newEmptyHeaderField("retry-after"), HpackStaticTable.newEmptyHeaderField("server"), HpackStaticTable.newEmptyHeaderField("set-cookie"), HpackStaticTable.newEmptyHeaderField("strict-transport-security"), HpackStaticTable.newEmptyHeaderField("transfer-encoding"), HpackStaticTable.newEmptyHeaderField("user-agent"), HpackStaticTable.newEmptyHeaderField("vary"), HpackStaticTable.newEmptyHeaderField("via"), HpackStaticTable.newEmptyHeaderField("www-authenticate"));
        HEADER_NAMES_TABLE_SHIFT = PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 22 : 18;
        HEADER_NAMES = new HeaderNameIndex[512];
        for (index = STATIC_TABLE.size(); index > 0; --index) {
            entry = HpackStaticTable.getEntry(index);
            bucket = HpackStaticTable.headerNameBucket(entry.name);
            tableEntry = HEADER_NAMES[bucket];
            if (tableEntry != null && !HpackUtil.equalsVariableTime(((HeaderNameIndex)tableEntry).name, entry.name)) {
                throw new IllegalStateException("Hash bucket collision between " + ((HeaderNameIndex)tableEntry).name + " and " + entry.name);
            }
            HpackStaticTable.HEADER_NAMES[bucket] = new HeaderNameIndex(entry.name, index, entry.value.length() == 0);
        }
        HEADERS_WITH_NON_EMPTY_VALUES_TABLE_SHIFT = PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 0 : 6;
        HEADERS_WITH_NON_EMPTY_VALUES = new HeaderIndex[64];
        for (index = STATIC_TABLE.size(); index > 0; --index) {
            entry = HpackStaticTable.getEntry(index);
            if (entry.value.length() <= 0) continue;
            bucket = HpackStaticTable.headerBucket(entry.value);
            tableEntry = HEADERS_WITH_NON_EMPTY_VALUES[bucket];
            if (tableEntry != null) {
                throw new IllegalStateException("Hash bucket collision between " + ((HeaderIndex)tableEntry).value + " and " + entry.value);
            }
            HpackStaticTable.HEADERS_WITH_NON_EMPTY_VALUES[bucket] = new HeaderIndex(entry.name, entry.value, index);
        }
        length = STATIC_TABLE.size();
    }

    private static final class HeaderIndex {
        final CharSequence name;
        final CharSequence value;
        final int index;

        HeaderIndex(CharSequence name, CharSequence value, int index) {
            this.name = name;
            this.value = value;
            this.index = index;
        }
    }

    private static final class HeaderNameIndex {
        final CharSequence name;
        final int index;
        final boolean emptyValue;

        HeaderNameIndex(CharSequence name, int index, boolean emptyValue) {
            this.name = name;
            this.index = index;
            this.emptyValue = emptyValue;
        }
    }
}

