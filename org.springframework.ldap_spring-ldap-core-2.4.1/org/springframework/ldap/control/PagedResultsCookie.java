/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.control;

import java.util.Arrays;

public class PagedResultsCookie {
    private byte[] cookie;

    public PagedResultsCookie(byte[] cookie) {
        this.cookie = (byte[])(cookie != null ? Arrays.copyOf(cookie, cookie.length) : null);
    }

    public byte[] getCookie() {
        if (this.cookie != null) {
            return Arrays.copyOf(this.cookie, this.cookie.length);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PagedResultsCookie that = (PagedResultsCookie)o;
        return Arrays.equals(this.cookie, that.cookie);
    }

    public int hashCode() {
        return this.cookie != null ? Arrays.hashCode(this.cookie) : 0;
    }
}

