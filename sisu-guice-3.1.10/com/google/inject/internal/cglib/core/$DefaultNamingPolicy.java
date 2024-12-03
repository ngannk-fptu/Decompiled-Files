/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$NamingPolicy;
import com.google.inject.internal.cglib.core.$Predicate;

public class $DefaultNamingPolicy
implements $NamingPolicy {
    public static final $DefaultNamingPolicy INSTANCE = new $DefaultNamingPolicy();

    public String getClassName(String prefix, String source, Object key, $Predicate names) {
        String base;
        if (prefix == null) {
            prefix = "com.google.inject.internal.cglib.empty.$Object";
        } else if (prefix.startsWith("java")) {
            prefix = "$" + prefix;
        }
        String attempt = base = prefix + "$$" + source.substring(source.lastIndexOf(46) + 1) + this.getTag() + "$$" + Integer.toHexString(key.hashCode());
        int index = 2;
        while (names.evaluate(attempt)) {
            attempt = base + "_" + index++;
        }
        return attempt;
    }

    protected String getTag() {
        return "ByCGLIB";
    }

    public int hashCode() {
        return this.getTag().hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof $DefaultNamingPolicy && (($DefaultNamingPolicy)o).getTag().equals(this.getTag());
    }
}

