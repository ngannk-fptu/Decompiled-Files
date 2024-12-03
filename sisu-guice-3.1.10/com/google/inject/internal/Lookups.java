/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface Lookups {
    public <T> Provider<T> getProvider(Key<T> var1);

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> var1);
}

