/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x500.style;

public class X500NameTokenizer {
    private String value;
    private int index;
    private char separator;
    private StringBuffer buf = new StringBuffer();

    public X500NameTokenizer(String oid) {
        this(oid, ',');
    }

    public X500NameTokenizer(String oid, char separator) {
        this.value = oid;
        this.index = -1;
        this.separator = separator;
    }

    public boolean hasMoreTokens() {
        return this.index != this.value.length();
    }

    public String nextToken() {
        int end;
        if (this.index == this.value.length()) {
            return null;
        }
        boolean quoted = false;
        boolean escaped = false;
        this.buf.setLength(0);
        for (end = this.index + 1; end != this.value.length(); ++end) {
            char c = this.value.charAt(end);
            if (c == '\"') {
                if (!escaped) {
                    quoted = !quoted;
                }
                this.buf.append(c);
                escaped = false;
                continue;
            }
            if (escaped || quoted) {
                this.buf.append(c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                this.buf.append(c);
                escaped = true;
                continue;
            }
            if (c == this.separator) break;
            this.buf.append(c);
        }
        this.index = end;
        return this.buf.toString();
    }
}

