/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IClientCertificate;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

final class ClientCertificate
implements IClientCertificate {
    private static final int MIN_KEY_SIZE_IN_BITS = 2048;
    public static final String DEFAULT_PKCS12_PASSWORD = "";
    private final PrivateKey privateKey;
    private final List<X509Certificate> publicKeyCertificateChain;

    ClientCertificate(PrivateKey privateKey, List<X509Certificate> publicKeyCertificateChain) {
        block8: {
            if (privateKey == null) {
                throw new NullPointerException("PrivateKey is null or empty");
            }
            this.privateKey = privateKey;
            if (privateKey instanceof RSAPrivateKey) {
                if (((RSAPrivateKey)privateKey).getModulus().bitLength() < 2048) {
                    throw new IllegalArgumentException("certificate key size must be at least 2048");
                }
            } else {
                if ("sun.security.mscapi.RSAPrivateKey".equals(privateKey.getClass().getName()) || "sun.security.mscapi.CPrivateKey".equals(privateKey.getClass().getName())) {
                    try {
                        Method method = privateKey.getClass().getMethod("length", new Class[0]);
                        method.setAccessible(true);
                        if ((Integer)method.invoke((Object)privateKey, new Object[0]) < 2048) {
                            throw new IllegalArgumentException("certificate key size must be at least 2048");
                        }
                        break block8;
                    }
                    catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                        throw new RuntimeException("error accessing sun.security.mscapi.RSAPrivateKey length: " + ex.getMessage());
                    }
                }
                throw new IllegalArgumentException("certificate key must be an instance of java.security.interfaces.RSAPrivateKey or sun.security.mscapi.RSAPrivateKey");
            }
        }
        this.publicKeyCertificateChain = publicKeyCertificateChain;
    }

    @Override
    public String publicCertificateHash() throws CertificateEncodingException, NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(ClientCertificate.getHash(this.publicKeyCertificateChain.get(0).getEncoded()));
    }

    @Override
    public List<String> getEncodedPublicKeyCertificateChain() throws CertificateEncodingException {
        ArrayList<String> result = new ArrayList<String>();
        for (X509Certificate cert : this.publicKeyCertificateChain) {
            result.add(Base64.getEncoder().encodeToString(cert.getEncoded()));
        }
        return result;
    }

    static ClientCertificate create(InputStream pkcs12Certificate, String password) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        if (password == null) {
            password = DEFAULT_PKCS12_PASSWORD;
        }
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(pkcs12Certificate, password.toCharArray());
        String alias = ClientCertificate.getPrivateKeyAlias(keystore);
        ArrayList<X509Certificate> publicKeyCertificateChain = new ArrayList<X509Certificate>();
        PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, password.toCharArray());
        X509Certificate publicKeyCertificate = (X509Certificate)keystore.getCertificate(alias);
        Certificate[] chain = keystore.getCertificateChain(alias);
        if (chain != null && chain.length > 0) {
            for (Certificate c : chain) {
                publicKeyCertificateChain.add((X509Certificate)c);
            }
        } else {
            publicKeyCertificateChain.add(publicKeyCertificate);
        }
        return new ClientCertificate(privateKey, publicKeyCertificateChain);
    }

    static String getPrivateKeyAlias(KeyStore keystore) throws KeyStoreException {
        String alias = null;
        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            String currentAlias = aliases.nextElement();
            if (!keystore.entryInstanceOf(currentAlias, KeyStore.PrivateKeyEntry.class)) continue;
            if (alias != null) {
                throw new IllegalArgumentException("more than one certificate alias found in input stream");
            }
            alias = currentAlias;
        }
        if (alias == null) {
            throw new IllegalArgumentException("certificate not loaded from input stream");
        }
        return alias;
    }

    static ClientCertificate create(PrivateKey key, X509Certificate publicKeyCertificate) {
        return new ClientCertificate(key, Arrays.asList(publicKeyCertificate));
    }

    private static byte[] getHash(byte[] inputBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(inputBytes);
        return md.digest();
    }

    @Override
    public PrivateKey privateKey() {
        return this.privateKey;
    }
}

