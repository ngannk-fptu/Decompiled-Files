/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 */
package com.nimbusds.jose.crypto.bc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BouncyCastleProviderSingleton {
    private static BouncyCastleProvider bouncyCastleProvider;

    private BouncyCastleProviderSingleton() {
    }

    public static BouncyCastleProvider getInstance() {
        if (bouncyCastleProvider != null) {
            return bouncyCastleProvider;
        }
        bouncyCastleProvider = new BouncyCastleProvider();
        return bouncyCastleProvider;
    }
}

