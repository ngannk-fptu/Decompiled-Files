/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

public enum BitShuffleType {
    BYTE(1),
    SHORT(2),
    INT(4),
    LONG(8),
    FLOAT(4),
    DOUBLE(8);

    public final int id;

    private BitShuffleType(int n2) {
        this.id = n2;
    }

    public int getTypeSize() {
        return this.id;
    }
}

