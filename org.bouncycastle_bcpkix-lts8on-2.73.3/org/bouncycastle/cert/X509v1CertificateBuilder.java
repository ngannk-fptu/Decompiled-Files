/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.Time
 *  org.bouncycastle.asn1.x509.V1TBSCertificateGenerator
 */
package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;

public class X509v1CertificateBuilder {
    private V1TBSCertificateGenerator tbsGen;

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore), new Time(notAfter), subject, publicKeyInfo);
    }

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore, dateLocale), new Time(notAfter, dateLocale), subject, publicKeyInfo);
    }

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        if (issuer == null) {
            throw new IllegalArgumentException("issuer must not be null");
        }
        if (publicKeyInfo == null) {
            throw new IllegalArgumentException("publicKeyInfo must not be null");
        }
        this.tbsGen = new V1TBSCertificateGenerator();
        this.tbsGen.setSerialNumber(new ASN1Integer(serial));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(notBefore);
        this.tbsGen.setEndDate(notAfter);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(publicKeyInfo);
    }

    public X509CertificateHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        return CertUtils.generateFullCert(signer, this.tbsGen.generateTBSCertificate());
    }
}

