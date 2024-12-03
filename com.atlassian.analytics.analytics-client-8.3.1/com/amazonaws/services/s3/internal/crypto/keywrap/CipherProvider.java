/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.util.Throwables;
import java.security.Provider;
import javax.crypto.Cipher;

public class CipherProvider {
    private final String algorithm;
    private final Provider provider;

    private CipherProvider(String algorithm, Provider provider) {
        this.algorithm = algorithm;
        this.provider = provider;
    }

    public static CipherProvider create(String algorithm) {
        return new CipherProvider(algorithm, null);
    }

    public static CipherProvider create(String algorithm, Provider provider) {
        return new CipherProvider(algorithm, provider);
    }

    public String algorithm() {
        return this.algorithm;
    }

    public Provider provider() {
        return this.provider;
    }

    public Cipher createCipher() {
        try {
            if (this.provider == null) {
                return Cipher.getInstance(this.algorithm);
            }
            return Cipher.getInstance(this.algorithm, this.provider);
        }
        catch (Exception e) {
            throw Throwables.failure(e, "An exception was thrown during the creation of a new Cipher for '" + this.provider + "'");
        }
    }
}

