/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.annotation.SdkProtectedApi;

@SdkProtectedApi
public interface SdkFunction<Input, Output> {
    public Output apply(Input var1);
}

