/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import java.io.Serializable;
import org.springframework.lang.Nullable;

public final class AutowiredPropertyMarker
implements Serializable {
    public static final Object INSTANCE = new AutowiredPropertyMarker();

    private AutowiredPropertyMarker() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return AutowiredPropertyMarker.class.hashCode();
    }

    public String toString() {
        return "(autowired)";
    }
}

