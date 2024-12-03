/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.Time
 */
package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaX509v3CertificateBuilder
extends X509v3CertificateBuilder {
    public JcaX509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKey) {
        super(issuer, serial, notBefore, notAfter, subject, publicKey);
    }

    public JcaX509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, PublicKey publicKey) {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public JcaX509v3CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, PublicKey publicKey) {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public JcaX509v3CertificateBuilder(X500Principal issuer, BigInteger serial, Date notBefore, Date notAfter, X500Principal subject, PublicKey publicKey) {
        super(X500Name.getInstance((Object)issuer.getEncoded()), serial, notBefore, notAfter, X500Name.getInstance((Object)subject.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public JcaX509v3CertificateBuilder(X509Certificate issuerCert, BigInteger serial, Date notBefore, Date notAfter, X500Principal subject, PublicKey publicKey) {
        this(issuerCert.getSubjectX500Principal(), serial, notBefore, notAfter, subject, publicKey);
    }

    public JcaX509v3CertificateBuilder(X509Certificate issuerCert, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, PublicKey publicKey) {
        this(X500Name.getInstance((Object)issuerCert.getSubjectX500Principal().getEncoded()), serial, notBefore, notAfter, subject, publicKey);
    }

    public JcaX509v3CertificateBuilder(X509Certificate template) throws CertificateEncodingException {
        super(new JcaX509CertificateHolder(template));
    }

    public JcaX509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier oid, boolean critical, X509Certificate certificate) throws CertificateEncodingException {
        this.copyAndAddExtension(oid, critical, new JcaX509CertificateHolder(certificate));
        return this;
    }
}

