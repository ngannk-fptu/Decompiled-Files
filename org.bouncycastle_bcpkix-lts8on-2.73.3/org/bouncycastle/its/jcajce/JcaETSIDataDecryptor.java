/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.spec.IESKEMParameterSpec
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.its.jcajce.ClassUtil;
import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.util.Arrays;

public class JcaETSIDataDecryptor
implements ETSIDataDecryptor {
    private final PrivateKey privateKey;
    private final JcaJceHelper helper;
    private final byte[] recipientHash;
    private SecretKey secretKey = null;

    JcaETSIDataDecryptor(PrivateKey recipientInfo, byte[] recipientHash, JcaJceHelper provider) {
        this.privateKey = recipientInfo;
        this.helper = provider;
        this.recipientHash = recipientHash;
    }

    @Override
    public byte[] decrypt(byte[] wrappedKey, byte[] content, byte[] nonce) {
        try {
            Cipher etsiKem = this.helper.createCipher("ETSIKEMwithSHA256");
            etsiKem.init(4, (Key)this.privateKey, (AlgorithmParameterSpec)new IESKEMParameterSpec(this.recipientHash));
            this.secretKey = (SecretKey)etsiKem.unwrap(wrappedKey, "AES", 3);
            Cipher ccm = this.helper.createCipher("CCM");
            ccm.init(2, (Key)this.secretKey, ClassUtil.getGCMSpec(nonce, 128));
            return ccm.doFinal(content);
        }
        catch (Exception gex) {
            throw new RuntimeException(gex.getMessage(), gex);
        }
    }

    @Override
    public byte[] getKey() {
        if (this.secretKey == null) {
            throw new IllegalStateException("no secret key recovered");
        }
        return this.secretKey.getEncoded();
    }

    public static Builder builder(PrivateKey privateKey, byte[] recipientHash) {
        return new Builder(privateKey, recipientHash);
    }

    public static class Builder {
        private JcaJceHelper provider;
        private final byte[] recipientHash;
        private final PrivateKey key;

        public Builder(PrivateKey key, byte[] recipientHash) {
            this.key = key;
            this.recipientHash = Arrays.clone((byte[])recipientHash);
        }

        public Builder provider(Provider provider) {
            this.provider = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder provider(String provider) {
            this.provider = new NamedJcaJceHelper(provider);
            return this;
        }

        public JcaETSIDataDecryptor build() {
            return new JcaETSIDataDecryptor(this.key, this.recipientHash, this.provider);
        }
    }
}

