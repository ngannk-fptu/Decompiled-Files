/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;

public class JcaMiscPEMGenerator
extends MiscPEMGenerator {
    private Object obj;
    private String algorithm;
    private char[] password;
    private SecureRandom random;
    private Provider provider;

    public JcaMiscPEMGenerator(Object o) throws IOException {
        super(JcaMiscPEMGenerator.convertObject(o));
    }

    public JcaMiscPEMGenerator(Object o, PEMEncryptor encryptor) throws IOException {
        super(JcaMiscPEMGenerator.convertObject(o), encryptor);
    }

    private static Object convertObject(Object o) throws IOException {
        if (o instanceof X509Certificate) {
            try {
                return new JcaX509CertificateHolder((X509Certificate)o);
            }
            catch (CertificateEncodingException e) {
                throw new IllegalArgumentException("Cannot encode object: " + e.toString());
            }
        }
        if (o instanceof X509CRL) {
            try {
                return new JcaX509CRLHolder((X509CRL)o);
            }
            catch (CRLException e) {
                throw new IllegalArgumentException("Cannot encode object: " + e.toString());
            }
        }
        if (o instanceof KeyPair) {
            return JcaMiscPEMGenerator.convertObject(((KeyPair)o).getPrivate());
        }
        if (o instanceof PrivateKey) {
            return PrivateKeyInfo.getInstance((Object)((Key)o).getEncoded());
        }
        if (o instanceof PublicKey) {
            return SubjectPublicKeyInfo.getInstance((Object)((PublicKey)o).getEncoded());
        }
        return o;
    }
}

