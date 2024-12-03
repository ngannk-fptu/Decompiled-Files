/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.its.jcajce.ClassUtil;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JceETSIDataEncryptor
implements ETSIDataEncryptor {
    private final SecureRandom random;
    private final JcaJceHelper helper;
    private byte[] nonce;
    private byte[] key;

    private JceETSIDataEncryptor(SecureRandom random, JcaJceHelper helper) {
        this.random = random;
        this.helper = helper;
    }

    @Override
    public byte[] encrypt(byte[] content) {
        this.key = new byte[16];
        this.random.nextBytes(this.key);
        this.nonce = new byte[12];
        this.random.nextBytes(this.nonce);
        try {
            SecretKeySpec k = new SecretKeySpec(this.key, "AES");
            Cipher ccm = this.helper.createCipher("CCM");
            ccm.init(1, (Key)k, ClassUtil.getGCMSpec(this.nonce, 128));
            return ccm.doFinal(content);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] getKey() {
        return this.key;
    }

    @Override
    public byte[] getNonce() {
        return this.nonce;
    }

    public static class Builder {
        private SecureRandom random;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setRandom(SecureRandom random) {
            this.random = random;
            return this;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String providerName) {
            this.helper = new NamedJcaJceHelper(providerName);
            return this;
        }

        public JceETSIDataEncryptor build() {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            return new JceETSIDataEncryptor(this.random, this.helper);
        }
    }
}

