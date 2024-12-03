/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections.impl;

public class IntRange {
    int begin;
    int end;

    public IntRange(int n, int n2) {
        this.begin = n;
        this.end = n2;
    }

    public String toString() {
        return this.begin + ".." + this.end;
    }
}

