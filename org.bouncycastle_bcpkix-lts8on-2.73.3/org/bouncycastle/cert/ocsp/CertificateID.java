/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.ocsp.CertID
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CertificateID {
    public static final AlgorithmIdentifier HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    private final CertID id;

    public CertificateID(CertID id) {
        if (id == null) {
            throw new IllegalArgumentException("'id' cannot be null");
        }
        this.id = id;
    }

    public CertificateID(DigestCalculator digestCalculator, X509CertificateHolder issuerCert, BigInteger number) throws OCSPException {
        this.id = CertificateID.createCertID(digestCalculator, issuerCert, new ASN1Integer(number));
    }

    public ASN1ObjectIdentifier getHashAlgOID() {
        return this.id.getHashAlgorithm().getAlgorithm();
    }

    public byte[] getIssuerNameHash() {
        return this.id.getIssuerNameHash().getOctets();
    }

    public byte[] getIssuerKeyHash() {
        return this.id.getIssuerKeyHash().getOctets();
    }

    public BigInteger getSerialNumber() {
        return this.id.getSerialNumber().getValue();
    }

    public boolean matchesIssuer(X509CertificateHolder issuerCert, DigestCalculatorProvider digCalcProvider) throws OCSPException {
        try {
            return CertificateID.createCertID(digCalcProvider.get(this.id.getHashAlgorithm()), issuerCert, this.id.getSerialNumber()).equals((Object)this.id);
        }
        catch (OperatorCreationException e) {
            throw new OCSPException("unable to create digest calculator: " + e.getMessage(), e);
        }
    }

    public CertID toASN1Primitive() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (!(o instanceof CertificateID)) {
            return false;
        }
        CertificateID obj = (CertificateID)o;
        return this.id.toASN1Primitive().equals(obj.id.toASN1Primitive());
    }

    public int hashCode() {
        return this.id.toASN1Primitive().hashCode();
    }

    public static CertificateID deriveCertificateID(CertificateID original, BigInteger newSerialNumber) {
        return new CertificateID(new CertID(original.id.getHashAlgorithm(), original.id.getIssuerNameHash(), original.id.getIssuerKeyHash(), new ASN1Integer(newSerialNumber)));
    }

    private static CertID createCertID(DigestCalculator digCalc, X509CertificateHolder issuerCert, ASN1Integer serialNumber) throws OCSPException {
        try {
            OutputStream dgOut = digCalc.getOutputStream();
            dgOut.write(issuerCert.toASN1Structure().getSubject().getEncoded("DER"));
            dgOut.close();
            DEROctetString issuerNameHash = new DEROctetString(digCalc.getDigest());
            SubjectPublicKeyInfo info = issuerCert.getSubjectPublicKeyInfo();
            dgOut = digCalc.getOutputStream();
            dgOut.write(info.getPublicKeyData().getBytes());
            dgOut.close();
            DEROctetString issuerKeyHash = new DEROctetString(digCalc.getDigest());
            return new CertID(digCalc.getAlgorithmIdentifier(), (ASN1OctetString)issuerNameHash, (ASN1OctetString)issuerKeyHash, serialNumber);
        }
        catch (Exception e) {
            throw new OCSPException("problem creating ID: " + e, e);
        }
    }
}

