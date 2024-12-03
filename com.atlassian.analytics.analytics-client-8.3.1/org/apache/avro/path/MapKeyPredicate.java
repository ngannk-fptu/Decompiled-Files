/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import org.apache.avro.path.PositionalPathPredicate;

public class MapKeyPredicate
implements PositionalPathPredicate {
    private final String key;

    public MapKeyPredicate(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        if (this.key == null) {
            return "";
        }
        return "[\"" + this.key + "\"]";
    }
}

