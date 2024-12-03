/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.PEMUtilities;

public class JcePEMEncryptorBuilder {
    private final String algorithm;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private SecureRandom random;

    public JcePEMEncryptorBuilder(String algorithm) {
        this.algorithm = algorithm;
    }

    public JcePEMEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePEMEncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePEMEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public PEMEncryptor build(final char[] password) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        int ivLength = this.algorithm.startsWith("AES-") ? 16 : 8;
        final byte[] iv = new byte[ivLength];
        this.random.nextBytes(iv);
        return new PEMEncryptor(){

            @Override
            public String getAlgorithm() {
                return JcePEMEncryptorBuilder.this.algorithm;
            }

            @Override
            public byte[] getIV() {
                return iv;
            }

            @Override
            public byte[] encrypt(byte[] encoding) throws PEMException {
                return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, encoding, password, JcePEMEncryptorBuilder.this.algorithm, iv);
            }
        };
    }
}

