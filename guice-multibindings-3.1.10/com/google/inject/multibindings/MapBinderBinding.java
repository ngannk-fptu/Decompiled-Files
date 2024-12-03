/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Binding
 *  com.google.inject.Key
 *  com.google.inject.TypeLiteral
 *  com.google.inject.spi.Element
 */
package com.google.inject.multibindings;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Element;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MapBinderBinding<T> {
    public Key<T> getMapKey();

    public TypeLiteral<?> getKeyTypeLiteral();

    public TypeLiteral<?> getValueTypeLiteral();

    public List<Map.Entry<?, Binding<?>>> getEntries();

    public boolean permitsDuplicates();

    public boolean containsElement(Element var1);
}

