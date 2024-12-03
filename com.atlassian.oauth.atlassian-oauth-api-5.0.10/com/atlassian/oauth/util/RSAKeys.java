/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.oauth.util;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RSAKeys {
    private static final String RSA = "RSA";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);
        return gen.generateKeyPair();
    }

    public static PublicKey fromPemEncodingToPublicKey(String pemEncodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory fac = KeyFactory.getInstance(RSA);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64((byte[])RSAKeys.convertFromOpenSsl(pemEncodedPublicKey).getBytes()));
        return fac.generatePublic(publicKeySpec);
    }

    public static PublicKey fromEncodedCertificateToPublicKey(String encodedCertificate) throws CertificateException {
        CertificateFactory certFac = CertificateFactory.getInstance("X509");
        ByteArrayInputStream in = new ByteArrayInputStream(encodedCertificate.getBytes());
        X509Certificate cert = (X509Certificate)certFac.generateCertificate(in);
        return cert.getPublicKey();
    }

    public static PrivateKey fromPemEncodingToPrivateKey(String pemEncodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory fac = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64((byte[])RSAKeys.convertFromOpenSsl(pemEncodedPrivateKey).getBytes()));
        return fac.generatePrivate(privateKeySpec);
    }

    public static String toPemEncoding(Key key) {
        return new String(Base64.encodeBase64((byte[])key.getEncoded()));
    }

    public static String convertFromOpenSsl(String key) {
        return key.replaceAll("-----[A-Z ]*-----", "").replace("\n", "");
    }
}

