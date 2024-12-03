/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KMSKeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperContext;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperProvider;
import com.amazonaws.services.s3.model.KMSEncryptionMaterials;

public class KMSKeyWrapperProvider
implements KeyWrapperProvider {
    private static final KMSKeyWrapperProvider DEFAULT = new KMSKeyWrapperProvider();

    private KMSKeyWrapperProvider() {
    }

    public static KMSKeyWrapperProvider create() {
        return DEFAULT;
    }

    @Override
    public InternalKeyWrapAlgorithm algorithm() {
        return InternalKeyWrapAlgorithm.KMS;
    }

    @Override
    public KeyWrapper createKeyWrapper(KeyWrapperContext keyWrapperContext) {
        return KMSKeyWrapper.builder().encryptionMaterials((KMSEncryptionMaterials)keyWrapperContext.materials()).kmsKeyWrapperContext(keyWrapperContext.kmsKeyWrapperContext()).build();
    }
}

