/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class CertificatePolicies
extends ASN1Object {
    private final PolicyInformation[] policyInformation;

    private static PolicyInformation[] copy(PolicyInformation[] policyInfo) {
        PolicyInformation[] result = new PolicyInformation[policyInfo.length];
        System.arraycopy(policyInfo, 0, result, 0, policyInfo.length);
        return result;
    }

    public static CertificatePolicies getInstance(Object obj) {
        if (obj instanceof CertificatePolicies) {
            return (CertificatePolicies)obj;
        }
        if (obj != null) {
            return new CertificatePolicies(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static CertificatePolicies getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CertificatePolicies.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static CertificatePolicies fromExtensions(Extensions extensions) {
        return CertificatePolicies.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.certificatePolicies));
    }

    public CertificatePolicies(PolicyInformation name) {
        this.policyInformation = new PolicyInformation[]{name};
    }

    public CertificatePolicies(PolicyInformation[] policyInformation) {
        this.policyInformation = CertificatePolicies.copy(policyInformation);
    }

    private CertificatePolicies(ASN1Sequence seq) {
        this.policyInformation = new PolicyInformation[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            this.policyInformation[i] = PolicyInformation.getInstance(seq.getObjectAt(i));
        }
    }

    public PolicyInformation[] getPolicyInformation() {
        return CertificatePolicies.copy(this.policyInformation);
    }

    public PolicyInformation getPolicyInformation(ASN1ObjectIdentifier policyIdentifier) {
        for (int i = 0; i != this.policyInformation.length; ++i) {
            if (!policyIdentifier.equals(this.policyInformation[i].getPolicyIdentifier())) continue;
            return this.policyInformation[i];
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.policyInformation);
    }

    public String toString() {
        StringBuffer p = new StringBuffer();
        for (int i = 0; i < this.policyInformation.length; ++i) {
            if (p.length() != 0) {
                p.append(", ");
            }
            p.append(this.policyInformation[i]);
        }
        return "CertificatePolicies: [" + p + "]";
    }
}

