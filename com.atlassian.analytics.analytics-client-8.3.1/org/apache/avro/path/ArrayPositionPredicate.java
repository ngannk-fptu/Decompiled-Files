/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import org.apache.avro.path.PositionalPathPredicate;

public class ArrayPositionPredicate
implements PositionalPathPredicate {
    private final long index;

    public ArrayPositionPredicate(long index) {
        this.index = index;
    }

    public String toString() {
        return "[" + this.index + "]";
    }
}

