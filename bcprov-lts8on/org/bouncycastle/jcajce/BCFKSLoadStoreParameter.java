/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.jcajce.BCLoadStoreParameter;

public class BCFKSLoadStoreParameter
extends BCLoadStoreParameter {
    private final PBKDFConfig storeConfig;
    private final EncryptionAlgorithm encAlg;
    private final MacAlgorithm macAlg;
    private final SignatureAlgorithm sigAlg;
    private final Key sigKey;
    private final X509Certificate[] certificates;
    private final CertChainValidator validator;

    private BCFKSLoadStoreParameter(Builder bldr) {
        super(bldr.in, bldr.out, bldr.protectionParameter);
        this.storeConfig = bldr.storeConfig;
        this.encAlg = bldr.encAlg;
        this.macAlg = bldr.macAlg;
        this.sigAlg = bldr.sigAlg;
        this.sigKey = bldr.sigKey;
        this.certificates = bldr.certs;
        this.validator = bldr.validator;
    }

    public PBKDFConfig getStorePBKDFConfig() {
        return this.storeConfig;
    }

    public EncryptionAlgorithm getStoreEncryptionAlgorithm() {
        return this.encAlg;
    }

    public MacAlgorithm getStoreMacAlgorithm() {
        return this.macAlg;
    }

    public SignatureAlgorithm getStoreSignatureAlgorithm() {
        return this.sigAlg;
    }

    public Key getStoreSignatureKey() {
        return this.sigKey;
    }

    public X509Certificate[] getStoreCertificates() {
        return this.certificates;
    }

    public CertChainValidator getCertChainValidator() {
        return this.validator;
    }

    public static class Builder {
        private final OutputStream out;
        private final InputStream in;
        private final KeyStore.ProtectionParameter protectionParameter;
        private final Key sigKey;
        private PBKDFConfig storeConfig = new PBKDF2Config.Builder().withIterationCount(16384).withSaltLength(64).withPRF(PBKDF2Config.PRF_SHA512).build();
        private EncryptionAlgorithm encAlg = EncryptionAlgorithm.AES256_CCM;
        private MacAlgorithm macAlg = MacAlgorithm.HmacSHA512;
        private SignatureAlgorithm sigAlg = SignatureAlgorithm.SHA512withECDSA;
        private X509Certificate[] certs = null;
        private CertChainValidator validator;

        public Builder() {
            this((OutputStream)null, (KeyStore.ProtectionParameter)null);
        }

        public Builder(OutputStream out, char[] password) {
            this(out, (KeyStore.ProtectionParameter)new KeyStore.PasswordProtection(password));
        }

        public Builder(OutputStream out, KeyStore.ProtectionParameter protectionParameter) {
            this.in = null;
            this.out = out;
            this.protectionParameter = protectionParameter;
            this.sigKey = null;
        }

        public Builder(OutputStream out, PrivateKey sigKey) {
            this.in = null;
            this.out = out;
            this.protectionParameter = null;
            this.sigKey = sigKey;
        }

        public Builder(InputStream in, PublicKey sigKey) {
            this.in = in;
            this.out = null;
            this.protectionParameter = null;
            this.sigKey = sigKey;
        }

        public Builder(InputStream in, CertChainValidator validator) {
            this.in = in;
            this.out = null;
            this.protectionParameter = null;
            this.validator = validator;
            this.sigKey = null;
        }

        public Builder(InputStream in, char[] password) {
            this(in, (KeyStore.ProtectionParameter)new KeyStore.PasswordProtection(password));
        }

        public Builder(InputStream in, KeyStore.ProtectionParameter protectionParameter) {
            this.in = in;
            this.out = null;
            this.protectionParameter = protectionParameter;
            this.sigKey = null;
        }

        public Builder withStorePBKDFConfig(PBKDFConfig storeConfig) {
            this.storeConfig = storeConfig;
            return this;
        }

        public Builder withStoreEncryptionAlgorithm(EncryptionAlgorithm encAlg) {
            this.encAlg = encAlg;
            return this;
        }

        public Builder withStoreMacAlgorithm(MacAlgorithm macAlg) {
            this.macAlg = macAlg;
            return this;
        }

        public Builder withCertificates(X509Certificate[] certs) {
            X509Certificate[] tmp = new X509Certificate[certs.length];
            System.arraycopy(certs, 0, tmp, 0, tmp.length);
            this.certs = tmp;
            return this;
        }

        public Builder withStoreSignatureAlgorithm(SignatureAlgorithm sigAlg) {
            this.sigAlg = sigAlg;
            return this;
        }

        public BCFKSLoadStoreParameter build() {
            return new BCFKSLoadStoreParameter(this);
        }
    }

    public static interface CertChainValidator {
        public boolean isValid(X509Certificate[] var1);
    }

    public static enum EncryptionAlgorithm {
        AES256_CCM,
        AES256_KWP;

    }

    public static enum MacAlgorithm {
        HmacSHA512,
        HmacSHA3_512;

    }

    public static enum SignatureAlgorithm {
        SHA512withDSA,
        SHA3_512withDSA,
        SHA512withECDSA,
        SHA3_512withECDSA,
        SHA512withRSA,
        SHA3_512withRSA;

    }
}

