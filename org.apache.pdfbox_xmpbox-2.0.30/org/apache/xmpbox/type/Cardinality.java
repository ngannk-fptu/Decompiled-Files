/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

public enum Cardinality {
    Simple(false),
    Bag(true),
    Seq(true),
    Alt(true);

    private final boolean array;

    private Cardinality(boolean a) {
        this.array = a;
    }

    public boolean isArray() {
        return this.array;
    }
}

