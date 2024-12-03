/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.binder;

import com.google.inject.Scope;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ScopedBindingBuilder {
    public void in(Class<? extends Annotation> var1);

    public void in(Scope var1);

    public void asEagerSingleton();
}

