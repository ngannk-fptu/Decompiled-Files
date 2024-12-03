/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Provider;
import com.google.inject.spi.HasDependencies;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ProviderWithDependencies<T>
extends Provider<T>,
HasDependencies {
}

