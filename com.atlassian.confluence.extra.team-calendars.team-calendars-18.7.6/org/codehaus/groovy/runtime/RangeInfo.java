/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

public class RangeInfo {
    public final int from;
    public final int to;
    public final boolean reverse;

    public RangeInfo(int from, int to, boolean reverse) {
        this.from = from;
        this.to = to;
        this.reverse = reverse;
    }
}

