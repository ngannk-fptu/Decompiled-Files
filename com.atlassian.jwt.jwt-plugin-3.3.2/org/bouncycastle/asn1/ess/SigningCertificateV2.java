/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class SigningCertificateV2
extends ASN1Object {
    ASN1Sequence certs;
    ASN1Sequence policies;

    public static SigningCertificateV2 getInstance(Object object) {
        if (object == null || object instanceof SigningCertificateV2) {
            return (SigningCertificateV2)object;
        }
        if (object instanceof ASN1Sequence) {
            return new SigningCertificateV2((ASN1Sequence)object);
        }
        return null;
    }

    private SigningCertificateV2(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 1 || aSN1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        this.certs = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() > 1) {
            this.policies = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public SigningCertificateV2(ESSCertIDv2 eSSCertIDv2) {
        this.certs = new DERSequence(eSSCertIDv2);
    }

    public SigningCertificateV2(ESSCertIDv2[] eSSCertIDv2Array) {
        this.certs = new DERSequence(eSSCertIDv2Array);
    }

    public SigningCertificateV2(ESSCertIDv2[] eSSCertIDv2Array, PolicyInformation[] policyInformationArray) {
        this.certs = new DERSequence(eSSCertIDv2Array);
        if (policyInformationArray != null) {
            this.policies = new DERSequence(policyInformationArray);
        }
    }

    public ESSCertIDv2[] getCerts() {
        ESSCertIDv2[] eSSCertIDv2Array = new ESSCertIDv2[this.certs.size()];
        for (int i = 0; i != this.certs.size(); ++i) {
            eSSCertIDv2Array[i] = ESSCertIDv2.getInstance(this.certs.getObjectAt(i));
        }
        return eSSCertIDv2Array;
    }

    public PolicyInformation[] getPolicies() {
        if (this.policies == null) {
            return null;
        }
        PolicyInformation[] policyInformationArray = new PolicyInformation[this.policies.size()];
        for (int i = 0; i != this.policies.size(); ++i) {
            policyInformationArray[i] = PolicyInformation.getInstance(this.policies.getObjectAt(i));
        }
        return policyInformationArray;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.certs);
        if (this.policies != null) {
            aSN1EncodableVector.add(this.policies);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

