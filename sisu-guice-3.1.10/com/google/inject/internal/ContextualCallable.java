/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface ContextualCallable<T> {
    public T call(InternalContext var1) throws ErrorsException;
}

