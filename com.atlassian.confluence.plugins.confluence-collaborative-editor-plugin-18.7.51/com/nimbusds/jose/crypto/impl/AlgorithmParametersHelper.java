/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public class AlgorithmParametersHelper {
    public static AlgorithmParameters getInstance(String name, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            return AlgorithmParameters.getInstance(name);
        }
        return AlgorithmParameters.getInstance(name, provider);
    }
}

