/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.internal.SdkThreadLocalsRegistry;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

public enum SigningAlgorithm {
    HmacSHA1,
    HmacSHA256;

    private final ThreadLocal<Mac> macReference;

    private SigningAlgorithm() {
        final String algorithmName = this.toString();
        this.macReference = SdkThreadLocalsRegistry.register(new ThreadLocal<Mac>(){

            @Override
            protected Mac initialValue() {
                try {
                    return Mac.getInstance(algorithmName);
                }
                catch (NoSuchAlgorithmException e) {
                    throw new SdkClientException("Unable to fetch Mac instance for Algorithm " + algorithmName + e.getMessage(), e);
                }
            }
        });
    }

    public Mac getMac() {
        return this.macReference.get();
    }
}

