/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

public interface InstrumentableClassLoader {
    public void addTransformer(ClassFileTransformer var1);

    public void removeTransformer(ClassFileTransformer var1);

    public ClassLoader copyWithoutTransformers();
}

