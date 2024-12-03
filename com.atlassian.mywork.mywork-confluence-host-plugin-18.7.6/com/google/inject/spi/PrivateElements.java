/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.spi.Element;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PrivateElements
extends Element {
    public List<Element> getElements();

    public Injector getInjector();

    public Set<Key<?>> getExposedKeys();

    public Object getExposedSource(Key<?> var1);
}

