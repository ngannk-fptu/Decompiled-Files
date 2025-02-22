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
import java.security.NoSuchProviderException;
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

public class NamedJcaJceHelper
implements JcaJceHelper {
    protected final String providerName;

    public NamedJcaJceHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public Cipher createCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance(algorithm, this.providerName);
    }

    @Override
    public Mac createMac(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Mac.getInstance(algorithm, this.providerName);
    }

    @Override
    public KeyAgreement createKeyAgreement(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyAgreement.getInstance(algorithm, this.providerName);
    }

    @Override
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameterGenerator.getInstance(algorithm, this.providerName);
    }

    @Override
    public AlgorithmParameters createAlgorithmParameters(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameters.getInstance(algorithm, this.providerName);
    }

    @Override
    public KeyGenerator createKeyGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyGenerator.getInstance(algorithm, this.providerName);
    }

    @Override
    public KeyFactory createKeyFactory(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyFactory.getInstance(algorithm, this.providerName);
    }

    @Override
    public SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecretKeyFactory.getInstance(algorithm, this.providerName);
    }

    @Override
    public KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyPairGenerator.getInstance(algorithm, this.providerName);
    }

    @Override
    public MessageDigest createMessageDigest(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance(algorithm, this.providerName);
    }

    @Override
    public Signature createSignature(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(algorithm, this.providerName);
    }

    @Override
    public CertificateFactory createCertificateFactory(String algorithm) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(algorithm, this.providerName);
    }

    @Override
    public SecureRandom createSecureRandom(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance(algorithm, this.providerName);
    }

    @Override
    public CertPathBuilder createCertPathBuilder(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return CertPathBuilder.getInstance(algorithm, this.providerName);
    }

    @Override
    public CertPathValidator createCertPathValidator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return CertPathValidator.getInstance(algorithm, this.providerName);
    }

    @Override
    public CertStore createCertStore(String type, CertStoreParameters params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        return CertStore.getInstance(type, params, this.providerName);
    }

    @Override
    public ExemptionMechanism createExemptionMechanism(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return ExemptionMechanism.getInstance(algorithm, this.providerName);
    }

    @Override
    public KeyStore createKeyStore(String type) throws KeyStoreException, NoSuchProviderException {
        return KeyStore.getInstance(type, this.providerName);
    }
}

