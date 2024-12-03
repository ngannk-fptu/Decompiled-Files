/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.ConstructionProxy;
import com.google.inject.internal.ErrorsException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface ConstructionProxyFactory<T> {
    public ConstructionProxy<T> create() throws ErrorsException;
}

