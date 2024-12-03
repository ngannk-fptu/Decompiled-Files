/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.StringUtil
 */
package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.function.Function;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;

public enum HttpHeaderValue {
    CLOSE("close"),
    CHUNKED("chunked"),
    GZIP("gzip"),
    IDENTITY("identity"),
    KEEP_ALIVE("keep-alive"),
    CONTINUE("100-continue"),
    PROCESSING("102-processing"),
    TE("TE"),
    BYTES("bytes"),
    NO_CACHE("no-cache"),
    UPGRADE("Upgrade");

    public static final Index<HttpHeaderValue> CACHE;
    private final String _string;
    private final ByteBuffer _buffer;
    private static final EnumSet<HttpHeader> __known;

    private HttpHeaderValue(String s) {
        this._string = s;
        this._buffer = BufferUtil.toBuffer((String)s);
    }

    public ByteBuffer toBuffer() {
        return this._buffer.asReadOnlyBuffer();
    }

    public boolean is(String s) {
        return this._string.equalsIgnoreCase(s);
    }

    public String asString() {
        return this._string;
    }

    public String toString() {
        return this._string;
    }

    public static boolean hasKnownValues(HttpHeader header) {
        if (header == null) {
            return false;
        }
        return __known.contains((Object)header);
    }

    public static boolean parseCsvIndex(String value, Function<HttpHeaderValue, Boolean> found) {
        return HttpHeaderValue.parseCsvIndex(value, found, null);
    }

    public static boolean parseCsvIndex(String value, Function<HttpHeaderValue, Boolean> found, Function<String, Boolean> unknown) {
        if (StringUtil.isBlank((String)value)) {
            return true;
        }
        int next = 0;
        block4: while (next < value.length()) {
            HttpHeaderValue token = (HttpHeaderValue)((Object)CACHE.getBest(value, next, value.length() - next));
            if (token != null) {
                int i = next + token.toString().length();
                block5: while (true) {
                    if (i >= value.length()) {
                        return found.apply(token);
                    }
                    switch (value.charAt(i)) {
                        case ',': {
                            if (!found.apply(token).booleanValue()) {
                                return false;
                            }
                            next = i + 1;
                            continue block4;
                        }
                        case ' ': {
                            break;
                        }
                        default: {
                            break block5;
                        }
                    }
                    ++i;
                }
            }
            if (' ' == value.charAt(next)) {
                ++next;
                continue;
            }
            int comma = value.indexOf(44, next);
            if (comma == next) {
                ++next;
                continue;
            }
            if (comma > next) {
                if (unknown == null) {
                    next = comma + 1;
                    continue;
                }
                String v = value.substring(next, comma).trim();
                if (StringUtil.isBlank((String)v) || unknown.apply(v).booleanValue()) {
                    next = comma + 1;
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    static {
        CACHE = new Index.Builder().caseSensitive(false).withAll((Object[])HttpHeaderValue.values(), HttpHeaderValue::toString).build();
        __known = EnumSet.of(HttpHeader.CONNECTION, HttpHeader.TRANSFER_ENCODING, HttpHeader.CONTENT_ENCODING);
    }
}

