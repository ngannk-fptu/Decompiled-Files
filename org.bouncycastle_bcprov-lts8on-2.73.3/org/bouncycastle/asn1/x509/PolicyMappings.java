/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertPolicyId;

public class PolicyMappings
extends ASN1Object {
    ASN1Sequence seq = null;

    public static PolicyMappings getInstance(Object obj) {
        if (obj instanceof PolicyMappings) {
            return (PolicyMappings)obj;
        }
        if (obj != null) {
            return new PolicyMappings(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PolicyMappings(ASN1Sequence seq) {
        this.seq = seq;
    }

    public PolicyMappings(CertPolicyId issuerDomainPolicy, CertPolicyId subjectDomainPolicy) {
        ASN1EncodableVector dv = new ASN1EncodableVector(2);
        dv.add(issuerDomainPolicy);
        dv.add(subjectDomainPolicy);
        this.seq = new DERSequence(new DERSequence(dv));
    }

    public PolicyMappings(CertPolicyId[] issuerDomainPolicy, CertPolicyId[] subjectDomainPolicy) {
        ASN1EncodableVector dev = new ASN1EncodableVector(issuerDomainPolicy.length);
        for (int i = 0; i != issuerDomainPolicy.length; ++i) {
            ASN1EncodableVector dv = new ASN1EncodableVector(2);
            dv.add(issuerDomainPolicy[i]);
            dv.add(subjectDomainPolicy[i]);
            dev.add(new DERSequence(dv));
        }
        this.seq = new DERSequence(dev);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}

