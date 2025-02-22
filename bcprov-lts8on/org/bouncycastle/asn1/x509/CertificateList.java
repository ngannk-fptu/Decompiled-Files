/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;

public class CertificateList
extends ASN1Object {
    TBSCertList tbsCertList;
    AlgorithmIdentifier sigAlgId;
    ASN1BitString sig;
    boolean isHashCodeSet = false;
    int hashCodeValue;

    public static CertificateList getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CertificateList.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static CertificateList getInstance(Object obj) {
        if (obj instanceof CertificateList) {
            return (CertificateList)obj;
        }
        if (obj != null) {
            return new CertificateList(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CertificateList(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("sequence wrong size for CertificateList");
        }
        this.tbsCertList = TBSCertList.getInstance(seq.getObjectAt(0));
        this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.sig = DERBitString.getInstance(seq.getObjectAt(2));
    }

    public TBSCertList getTBSCertList() {
        return this.tbsCertList;
    }

    public TBSCertList.CRLEntry[] getRevokedCertificates() {
        return this.tbsCertList.getRevokedCertificates();
    }

    public Enumeration getRevokedCertificateEnumeration() {
        return this.tbsCertList.getRevokedCertificateEnumeration();
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }

    public ASN1BitString getSignature() {
        return this.sig;
    }

    public int getVersionNumber() {
        return this.tbsCertList.getVersionNumber();
    }

    public X500Name getIssuer() {
        return this.tbsCertList.getIssuer();
    }

    public Time getThisUpdate() {
        return this.tbsCertList.getThisUpdate();
    }

    public Time getNextUpdate() {
        return this.tbsCertList.getNextUpdate();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.tbsCertList);
        v.add(this.sigAlgId);
        v.add(this.sig);
        return new DERSequence(v);
    }

    @Override
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.hashCodeValue = super.hashCode();
            this.isHashCodeSet = true;
        }
        return this.hashCodeValue;
    }
}

