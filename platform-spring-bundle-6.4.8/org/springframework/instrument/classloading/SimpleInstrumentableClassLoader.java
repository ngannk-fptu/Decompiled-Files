/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.WeavingTransformer;
import org.springframework.lang.Nullable;

public class SimpleInstrumentableClassLoader
extends OverridingClassLoader {
    private final WeavingTransformer weavingTransformer;

    public SimpleInstrumentableClassLoader(@Nullable ClassLoader parent) {
        super(parent);
        this.weavingTransformer = new WeavingTransformer(parent);
    }

    public void addTransformer(ClassFileTransformer transformer) {
        this.weavingTransformer.addTransformer(transformer);
    }

    @Override
    protected byte[] transformIfNecessary(String name, byte[] bytes) {
        return this.weavingTransformer.transformIfNecessary(name, bytes);
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}

