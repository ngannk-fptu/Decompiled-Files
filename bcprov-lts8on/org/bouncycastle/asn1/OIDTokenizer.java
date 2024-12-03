/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class OIDTokenizer {
    private String oid;
    private int index;

    public OIDTokenizer(String oid) {
        this.oid = oid;
        this.index = 0;
    }

    public boolean hasMoreTokens() {
        return this.index != -1;
    }

    public String nextToken() {
        if (this.index == -1) {
            return null;
        }
        int end = this.oid.indexOf(46, this.index);
        if (end == -1) {
            String token = this.oid.substring(this.index);
            this.index = -1;
            return token;
        }
        String token = this.oid.substring(this.index, end);
        this.index = end + 1;
        return token;
    }
}

