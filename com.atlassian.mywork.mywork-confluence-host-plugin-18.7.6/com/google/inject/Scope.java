/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Key;
import com.google.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Scope {
    public <T> Provider<T> scope(Key<T> var1, Provider<T> var2);

    public String toString();
}

