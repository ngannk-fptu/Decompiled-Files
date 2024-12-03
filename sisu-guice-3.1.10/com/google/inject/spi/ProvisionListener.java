/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.spi.DependencyAndSource;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ProvisionListener {
    public <T> void onProvision(ProvisionInvocation<T> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ProvisionInvocation<T> {
        public abstract Binding<T> getBinding();

        public abstract T provision();

        public abstract List<DependencyAndSource> getDependencyChain();
    }
}

