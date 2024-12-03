/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.InternalContext;
import com.google.inject.spi.InjectionPoint;

interface SingleMemberInjector {
    public void inject(Errors var1, InternalContext var2, Object var3);

    public InjectionPoint getInjectionPoint();
}

