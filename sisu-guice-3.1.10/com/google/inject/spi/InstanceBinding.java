/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface InstanceBinding<T>
extends Binding<T>,
HasDependencies {
    public T getInstance();

    public Set<InjectionPoint> getInjectionPoints();
}

