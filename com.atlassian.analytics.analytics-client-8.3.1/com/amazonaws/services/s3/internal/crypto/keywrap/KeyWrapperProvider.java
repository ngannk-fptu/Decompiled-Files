/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperContext;

public interface KeyWrapperProvider {
    public InternalKeyWrapAlgorithm algorithm();

    public KeyWrapper createKeyWrapper(KeyWrapperContext var1);
}

