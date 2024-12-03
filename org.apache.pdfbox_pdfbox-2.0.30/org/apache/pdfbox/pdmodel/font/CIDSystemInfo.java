/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

public final class CIDSystemInfo {
    private final String registry;
    private final String ordering;
    private final int supplement;

    public CIDSystemInfo(String registry, String ordering, int supplement) {
        this.registry = registry;
        this.ordering = ordering;
        this.supplement = supplement;
    }

    public String getRegistry() {
        return this.registry;
    }

    public String getOrdering() {
        return this.ordering;
    }

    public int getSupplement() {
        return this.supplement;
    }

    public String toString() {
        return this.getRegistry() + "-" + this.getOrdering() + "-" + this.getSupplement();
    }
}

