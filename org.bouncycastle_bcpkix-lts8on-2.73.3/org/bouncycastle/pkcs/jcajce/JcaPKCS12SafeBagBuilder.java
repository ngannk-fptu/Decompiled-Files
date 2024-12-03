/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.Certificate
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCSIOException;

public class JcaPKCS12SafeBagBuilder
extends PKCS12SafeBagBuilder {
    public JcaPKCS12SafeBagBuilder(X509Certificate certificate) throws IOException {
        super(JcaPKCS12SafeBagBuilder.convertCert(certificate));
    }

    private static Certificate convertCert(X509Certificate certificate) throws IOException {
        try {
            return Certificate.getInstance((Object)certificate.getEncoded());
        }
        catch (CertificateEncodingException e) {
            throw new PKCSIOException("cannot encode certificate: " + e.getMessage(), e);
        }
    }

    public JcaPKCS12SafeBagBuilder(PrivateKey privateKey, OutputEncryptor encryptor) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()), encryptor);
    }

    public JcaPKCS12SafeBagBuilder(PrivateKey privateKey) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()));
    }
}

