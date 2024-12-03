/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation.transport;

import io.micrometer.common.lang.Nullable;

public interface Propagator {

    public static interface Getter<C> {
        @Nullable
        public String get(C var1, String var2);
    }

    public static interface Setter<C> {
        public void set(@Nullable C var1, String var2, String var3);
    }
}

