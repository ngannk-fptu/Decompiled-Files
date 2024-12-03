/*
 * Decompiled with CFR 0.152.
 */
package antlr.ASdebug;

public class TokenOffsetInfo {
    public final int beginOffset;
    public final int length;

    public TokenOffsetInfo(int n, int n2) {
        this.beginOffset = n;
        this.length = n2;
    }

    public int getEndOffset() {
        return this.beginOffset + this.length - 1;
    }
}

