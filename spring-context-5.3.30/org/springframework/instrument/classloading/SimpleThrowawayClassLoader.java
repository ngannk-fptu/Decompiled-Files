/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.OverridingClassLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.instrument.classloading;

import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;

public class SimpleThrowawayClassLoader
extends OverridingClassLoader {
    public SimpleThrowawayClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}

