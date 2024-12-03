/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.bouncycastle.openssl.PEMKeyPair
 *  org.bouncycastle.openssl.PEMParser
 *  org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
 */
package com.atlassian.jwt.core.keys;

import com.atlassian.jwt.exception.JwtCannotRetrieveKeyException;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class KeyUtils {
    public RSAPrivateKey readRsaPrivateKeyFromPem(Reader reader) throws JwtCannotRetrieveKeyException {
        PEMParser pemParser = new PEMParser(reader);
        try {
            Object object = pemParser.readObject();
            PEMKeyPair pemKeyPair = (PEMKeyPair)object;
            byte[] encodedPrivateKey = pemKeyPair.getPrivateKeyInfo().getEncoded();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            return (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
        }
        catch (Exception e) {
            throw new JwtCannotRetrieveKeyException("Error reading private key", e);
        }
    }

    public RSAPublicKey readRsaPublicKeyFromPem(Reader reader) throws JwtCannotRetrieveKeyException {
        PEMParser pemParser = new PEMParser(reader);
        try {
            Object object = pemParser.readObject();
            SubjectPublicKeyInfo pub = SubjectPublicKeyInfo.getInstance((Object)object);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            return (RSAPublicKey)converter.getPublicKey(pub);
        }
        catch (Exception e) {
            throw new JwtCannotRetrieveKeyException("Error reading public key", e);
        }
    }

    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}

