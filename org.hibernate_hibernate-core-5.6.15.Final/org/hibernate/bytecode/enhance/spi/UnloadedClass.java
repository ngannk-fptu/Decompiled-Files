/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import java.lang.annotation.Annotation;

public interface UnloadedClass {
    public boolean hasAnnotation(Class<? extends Annotation> var1);

    public String getName();
}

