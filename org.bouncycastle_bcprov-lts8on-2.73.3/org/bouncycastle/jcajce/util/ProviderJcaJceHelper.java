/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.ExemptionMechanism;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class ProviderJcaJceHelper
implements JcaJceHelper {
    protected final Provider provider;

    public ProviderJcaJceHelper(Provider provider) {
        this.provider = provider;
    }

    @Override
    public Cipher createCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(algorithm, this.provider);
    }

    @Override
    public Mac createMac(String algorithm) throws NoSuchAlgorithmException {
        return Mac.getInstance(algorithm, this.provider);
    }

    @Override
    public KeyAgreement createKeyAgreement(String algorithm) throws NoSuchAlgorithmException {
        return KeyAgreement.getInstance(algorithm, this.provider);
    }

    @Override
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String algorithm) throws NoSuchAlgorithmException {
        return AlgorithmParameterGenerator.getInstance(algorithm, this.provider);
    }

    @Override
    public AlgorithmParameters createAlgorithmParameters(String algorithm) throws NoSuchAlgorithmException {
        return AlgorithmParameters.getInstance(algorithm, this.provider);
    }

    @Override
    public KeyGenerator createKeyGenerator(String algorithm) throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(algorithm, this.provider);
    }

    @Override
    public KeyFactory createKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(algorithm, this.provider);
    }

    @Override
    public SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(algorithm, this.provider);
    }

    @Override
    public KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance(algorithm, this.provider);
    }

    @Override
    public MessageDigest createMessageDigest(String algorithm) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm, this.provider);
    }

    @Override
    public Signature createSignature(String algorithm) throws NoSuchAlgorithmException {
        return Signature.getInstance(algorithm, this.provider);
    }

    @Override
    public CertificateFactory createCertificateFactory(String algorithm) throws CertificateException {
        return CertificateFactory.getInstance(algorithm, this.provider);
    }

    @Override
    public SecureRandom createSecureRandom(String algorithm) throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(algorithm, this.provider);
    }

    @Override
    public CertPathBuilder createCertPathBuilder(String algorithm) throws NoSuchAlgorithmException {
        return CertPathBuilder.getInstance(algorithm, this.provider);
    }

    @Override
    public CertPathValidator createCertPathValidator(String algorithm) throws NoSuchAlgorithmException {
        return CertPathValidator.getInstance(algorithm, this.provider);
    }

    @Override
    public CertStore createCertStore(String type, CertStoreParameters params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return CertStore.getInstance(type, params, this.provider);
    }

    @Override
    public ExemptionMechanism createExemptionMechanism(String algorithm) throws NoSuchAlgorithmException {
        return ExemptionMechanism.getInstance(algorithm, this.provider);
    }

    @Override
    public KeyStore createKeyStore(String type) throws KeyStoreException {
        return KeyStore.getInstance(type, this.provider);
    }
}

