/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;

public class AttributeCertificate
extends ASN1Object {
    AttributeCertificateInfo acinfo;
    AlgorithmIdentifier signatureAlgorithm;
    ASN1BitString signatureValue;

    public static AttributeCertificate getInstance(Object obj) {
        if (obj instanceof AttributeCertificate) {
            return (AttributeCertificate)obj;
        }
        if (obj != null) {
            return new AttributeCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AttributeCertificate(AttributeCertificateInfo acinfo, AlgorithmIdentifier signatureAlgorithm, DERBitString signatureValue) {
        this.acinfo = acinfo;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signatureValue = signatureValue;
    }

    private AttributeCertificate(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.acinfo = AttributeCertificateInfo.getInstance(seq.getObjectAt(0));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.signatureValue = ASN1BitString.getInstance(seq.getObjectAt(2));
    }

    public AttributeCertificateInfo getAcinfo() {
        return this.acinfo;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public ASN1BitString getSignatureValue() {
        return this.signatureValue;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.acinfo);
        v.add(this.signatureAlgorithm);
        v.add(this.signatureValue);
        return new DERSequence(v);
    }
}

