/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AssistedMethod {
    public Method getFactoryMethod();

    public TypeLiteral<?> getImplementationType();

    public Constructor<?> getImplementationConstructor();

    public Set<Dependency<?>> getDependencies();
}

