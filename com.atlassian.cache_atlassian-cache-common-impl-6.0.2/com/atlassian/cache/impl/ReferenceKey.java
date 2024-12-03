/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.impl;

import java.io.Serializable;

public class ReferenceKey
implements Serializable {
    private static final long serialVersionUID = 3814781337421919991L;
    public static final ReferenceKey KEY = new ReferenceKey();

    private ReferenceKey() {
    }

    public boolean equals(Object o) {
        return o instanceof ReferenceKey;
    }

    public int hashCode() {
        return 0;
    }
}

