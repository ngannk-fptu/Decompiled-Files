/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import com.google.inject.Key;
import com.google.inject.assistedinject.AssistedMethod;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AssistedInjectBinding<T> {
    public Key<T> getKey();

    public Collection<AssistedMethod> getAssistedMethods();
}

