/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

public final class Size {
    private final long calculated;
    private final boolean exact;

    public Size(long calculated, boolean exact) {
        this.calculated = calculated;
        this.exact = exact;
    }

    public long getCalculated() {
        return this.calculated;
    }

    public boolean isExact() {
        return this.exact;
    }
}

