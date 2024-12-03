/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v1CertificateBuilder;

public class JcaX509v1CertificateBuilder
extends X509v1CertificateBuilder {
    public JcaX509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, PublicKey publicKey) {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public JcaX509v1CertificateBuilder(X500Principal issuer, BigInteger serial, Date notBefore, Date notAfter, X500Principal subject, PublicKey publicKey) {
        super(X500Name.getInstance((Object)issuer.getEncoded()), serial, notBefore, notAfter, X500Name.getInstance((Object)subject.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
}

