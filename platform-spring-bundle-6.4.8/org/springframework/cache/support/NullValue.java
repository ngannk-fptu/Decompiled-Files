/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.support;

import java.io.Serializable;
import org.springframework.lang.Nullable;

public final class NullValue
implements Serializable {
    public static final Object INSTANCE = new NullValue();
    private static final long serialVersionUID = 1L;

    private NullValue() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || obj == null;
    }

    public int hashCode() {
        return NullValue.class.hashCode();
    }

    public String toString() {
        return "null";
    }
}

