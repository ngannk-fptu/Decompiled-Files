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
import org.bouncycastle.asn1.ess.OtherCertID;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class OtherSigningCertificate
extends ASN1Object {
    ASN1Sequence certs;
    ASN1Sequence policies;

    public static OtherSigningCertificate getInstance(Object o) {
        if (o instanceof OtherSigningCertificate) {
            return (OtherSigningCertificate)((Object)o);
        }
        if (o != null) {
            return new OtherSigningCertificate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private OtherSigningCertificate(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.certs = ASN1Sequence.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.policies = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public OtherSigningCertificate(OtherCertID otherCertID) {
        this.certs = new DERSequence((ASN1Encodable)otherCertID);
    }

    public OtherCertID[] getCerts() {
        OtherCertID[] cs = new OtherCertID[this.certs.size()];
        for (int i = 0; i != this.certs.size(); ++i) {
            cs[i] = OtherCertID.getInstance(this.certs.getObjectAt(i));
        }
        return cs;
    }

    public PolicyInformation[] getPolicies() {
        if (this.policies == null) {
            return null;
        }
        PolicyInformation[] ps = new PolicyInformation[this.policies.size()];
        for (int i = 0; i != this.policies.size(); ++i) {
            ps[i] = PolicyInformation.getInstance((Object)this.policies.getObjectAt(i));
        }
        return ps;
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

