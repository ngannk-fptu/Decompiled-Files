/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.Filter;

public abstract class AbstractFilter
implements Filter {
    private static final int DEFAULT_BUFFER_SIZE = 256;

    @Override
    public String encode() {
        StringBuffer buf = new StringBuffer(256);
        buf = this.encode(buf);
        return buf.toString();
    }

    public String toString() {
        return this.encode();
    }
}

