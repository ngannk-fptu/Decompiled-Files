/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import org.apache.avro.path.PositionalPathPredicate;

public class UnionTypePredicate
implements PositionalPathPredicate {
    private final String type;

    public UnionTypePredicate(String type) {
        this.type = type;
    }

    public String toString() {
        return "[" + this.type + "]";
    }
}

