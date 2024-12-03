/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import javax.json.stream.JsonLocation;

class JsonLocationImpl
implements JsonLocation {
    static final JsonLocation UNKNOWN = new JsonLocationImpl(-1L, -1L, -1L);
    private final long columnNo;
    private final long lineNo;
    private final long offset;

    JsonLocationImpl(long lineNo, long columnNo, long streamOffset) {
        this.lineNo = lineNo;
        this.columnNo = columnNo;
        this.offset = streamOffset;
    }

    @Override
    public long getLineNumber() {
        return this.lineNo;
    }

    @Override
    public long getColumnNumber() {
        return this.columnNo;
    }

    @Override
    public long getStreamOffset() {
        return this.offset;
    }

    public String toString() {
        return "(line no=" + this.lineNo + ", column no=" + this.columnNo + ", offset=" + this.offset + ")";
    }
}

