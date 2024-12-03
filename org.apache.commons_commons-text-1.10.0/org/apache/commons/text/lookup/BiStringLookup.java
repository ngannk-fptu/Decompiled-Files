/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import org.apache.commons.text.lookup.StringLookup;

@FunctionalInterface
public interface BiStringLookup<U>
extends StringLookup {
    default public String lookup(String key, U object) {
        return this.lookup(key);
    }
}

