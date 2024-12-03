/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
 */
package com.nimbusds.jose.crypto.bc;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

public final class BouncyCastleFIPSProviderSingleton {
    private static BouncyCastleFipsProvider bouncyCastleFIPSProvider;

    private BouncyCastleFIPSProviderSingleton() {
    }

    public static BouncyCastleFipsProvider getInstance() {
        if (bouncyCastleFIPSProvider == null) {
            bouncyCastleFIPSProvider = new BouncyCastleFipsProvider();
        }
        return bouncyCastleFIPSProvider;
    }
}

