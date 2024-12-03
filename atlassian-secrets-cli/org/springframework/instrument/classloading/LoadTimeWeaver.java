/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;

public interface LoadTimeWeaver {
    public void addTransformer(ClassFileTransformer var1);

    public ClassLoader getInstrumentableClassLoader();

    public ClassLoader getThrowawayClassLoader();
}

