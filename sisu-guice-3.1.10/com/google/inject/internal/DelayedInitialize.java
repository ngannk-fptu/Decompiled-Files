/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;

interface DelayedInitialize {
    public void initialize(InjectorImpl var1, Errors var2) throws ErrorsException;
}

