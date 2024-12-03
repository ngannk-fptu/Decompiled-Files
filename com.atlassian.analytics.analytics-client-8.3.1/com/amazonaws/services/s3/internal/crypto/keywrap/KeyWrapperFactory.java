/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.internal.crypto.keywrap.AesGcmKeyWrapperProvider;
import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KMSKeyWrapperProvider;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperContext;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperProvider;
import com.amazonaws.services.s3.internal.crypto.keywrap.RsaOaepKeyWrapperProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyWrapperFactory {
    private static final KeyWrapperFactory DEFAULT = KeyWrapperFactory.builder().addKeyWrapper(AesGcmKeyWrapperProvider.create()).addKeyWrapper(RsaOaepKeyWrapperProvider.createSha1()).addKeyWrapper(KMSKeyWrapperProvider.create()).build();
    private final Map<InternalKeyWrapAlgorithm, KeyWrapperProvider> keyWrapperProviderMap;

    private KeyWrapperFactory(Builder b) {
        HashMap<InternalKeyWrapAlgorithm, KeyWrapperProvider> mutableKeyWrapperMap = new HashMap<InternalKeyWrapAlgorithm, KeyWrapperProvider>();
        for (KeyWrapperProvider keyWrapper : b.keyWrapperProviders) {
            mutableKeyWrapperMap.put(keyWrapper.algorithm(), keyWrapper);
        }
        this.keyWrapperProviderMap = Collections.unmodifiableMap(mutableKeyWrapperMap);
    }

    public static KeyWrapperFactory defaultInstance() {
        return DEFAULT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public KeyWrapper createKeyWrapper(KeyWrapperContext context) {
        KeyWrapperProvider keyWrapperProvider = this.keyWrapperProviderMap.get((Object)context.internalKeyWrapAlgorithm());
        if (keyWrapperProvider == null) {
            throw new SecurityException("A key wrapping algorithm could not be found for '" + (Object)((Object)context.internalKeyWrapAlgorithm()) + "'");
        }
        return keyWrapperProvider.createKeyWrapper(context);
    }

    public static class Builder {
        private Collection<KeyWrapperProvider> keyWrapperProviders;

        public Builder addKeyWrapper(KeyWrapperProvider keyWrapperProvider) {
            if (this.keyWrapperProviders == null) {
                this.keyWrapperProviders = new ArrayList<KeyWrapperProvider>();
            }
            this.keyWrapperProviders.add(keyWrapperProvider);
            return this;
        }

        public KeyWrapperFactory build() {
            return new KeyWrapperFactory(this);
        }
    }
}

