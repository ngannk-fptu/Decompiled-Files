/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.internal.crypto.CryptoUtils;
import com.amazonaws.services.s3.internal.crypto.keywrap.AesGcmKeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.CipherProvider;
import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperContext;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperProvider;

public final class AesGcmKeyWrapperProvider
implements KeyWrapperProvider {
    private static final AesGcmKeyWrapperProvider DEFAULT = new AesGcmKeyWrapperProvider();

    private AesGcmKeyWrapperProvider() {
    }

    public static AesGcmKeyWrapperProvider create() {
        return DEFAULT;
    }

    @Override
    public InternalKeyWrapAlgorithm algorithm() {
        return InternalKeyWrapAlgorithm.AES_GCM_NoPadding;
    }

    @Override
    public KeyWrapper createKeyWrapper(KeyWrapperContext keyWrapperContext) {
        String remappedCekAlgorithm = CryptoUtils.normalizeContentAlgorithmForValidation(keyWrapperContext.contentCryptoScheme().getCipherAlgorithm());
        return AesGcmKeyWrapper.builder().cipherProvider(CipherProvider.create(AesGcmKeyWrapper.cipherAlgorithm(), keyWrapperContext.cryptoProvider())).secureRandom(keyWrapperContext.secureRandom()).cekAlgorithm(remappedCekAlgorithm).build();
    }
}

