/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http.Header;

final class HPackHeader
implements Header {
    private static final int ENTRY_SIZE_OVERHEAD = 32;
    private final String name;
    private final int nameLen;
    private final String value;
    private final int valueLen;
    private final boolean sensitive;

    HPackHeader(String name, int nameLen, String value, int valueLen, boolean sensitive) {
        this.name = name;
        this.nameLen = nameLen;
        this.value = value;
        this.valueLen = valueLen;
        this.sensitive = sensitive;
    }

    HPackHeader(String name, String value, boolean sensitive) {
        this(name, name.length(), value, value.length(), sensitive);
    }

    HPackHeader(String name, String value) {
        this(name, value, false);
    }

    HPackHeader(Header header) {
        this(header.getName(), header.getValue(), header.isSensitive());
    }

    public String getName() {
        return this.name;
    }

    public int getNameLen() {
        return this.nameLen;
    }

    public String getValue() {
        return this.value;
    }

    public int getValueLen() {
        return this.valueLen;
    }

    public boolean isSensitive() {
        return this.sensitive;
    }

    public int getTotalSize() {
        return this.nameLen + this.valueLen + 32;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.name).append(": ");
        if (this.value != null) {
            buf.append(this.value);
        }
        return buf.toString();
    }
}

