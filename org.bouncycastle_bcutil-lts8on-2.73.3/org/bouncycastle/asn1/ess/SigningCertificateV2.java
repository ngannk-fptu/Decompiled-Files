/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.PolicyInformation
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
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

    public static SigningCertificateV2 getInstance(Object o) {
        if (o == null || o instanceof SigningCertificateV2) {
            return (SigningCertificateV2)((Object)o);
        }
        if (o instanceof ASN1Sequence) {
            return new SigningCertificateV2((ASN1Sequence)o);
        }
        return null;
    }

    private SigningCertificateV2(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.certs = ASN1Sequence.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.policies = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public SigningCertificateV2(ESSCertIDv2 cert) {
        this.certs = new DERSequence((ASN1Encodable)cert);
    }

    public SigningCertificateV2(ESSCertIDv2[] certs) {
        this.certs = new DERSequence((ASN1Encodable[])certs);
    }

    public SigningCertificateV2(ESSCertIDv2[] certs, PolicyInformation[] policies) {
        this.certs = new DERSequence((ASN1Encodable[])certs);
        if (policies != null) {
            this.policies = new DERSequence((ASN1Encodable[])policies);
        }
    }

    public ESSCertIDv2[] getCerts() {
        ESSCertIDv2[] certIds = new ESSCertIDv2[this.certs.size()];
        for (int i = 0; i != this.certs.size(); ++i) {
            certIds[i] = ESSCertIDv2.getInstance(this.certs.getObjectAt(i));
        }
        return certIds;
    }

    public PolicyInformation[] getPolicies() {
        if (this.policies == null) {
            return null;
        }
        PolicyInformation[] policyInformations = new PolicyInformation[this.policies.size()];
        for (int i = 0; i != this.policies.size(); ++i) {
            policyInformations[i] = PolicyInformation.getInstance((Object)this.policies.getObjectAt(i));
        }
        return policyInformations;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certs);
        if (this.policies != null) {
            v.add((ASN1Encodable)this.policies);
        }
        return new DERSequence(v);
    }
}

