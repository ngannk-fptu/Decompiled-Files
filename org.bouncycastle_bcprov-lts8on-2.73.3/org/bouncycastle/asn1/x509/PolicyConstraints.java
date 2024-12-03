/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class PolicyConstraints
extends ASN1Object {
    private BigInteger requireExplicitPolicyMapping;
    private BigInteger inhibitPolicyMapping;

    public PolicyConstraints(BigInteger requireExplicitPolicyMapping, BigInteger inhibitPolicyMapping) {
        this.requireExplicitPolicyMapping = requireExplicitPolicyMapping;
        this.inhibitPolicyMapping = inhibitPolicyMapping;
    }

    private PolicyConstraints(ASN1Sequence seq) {
        for (int i = 0; i != seq.size(); ++i) {
            ASN1TaggedObject to = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            if (to.getTagNo() == 0) {
                this.requireExplicitPolicyMapping = ASN1Integer.getInstance(to, false).getValue();
                continue;
            }
            if (to.getTagNo() == 1) {
                this.inhibitPolicyMapping = ASN1Integer.getInstance(to, false).getValue();
                continue;
            }
            throw new IllegalArgumentException("Unknown tag encountered.");
        }
    }

    public static PolicyConstraints getInstance(Object obj) {
        if (obj instanceof PolicyConstraints) {
            return (PolicyConstraints)obj;
        }
        if (obj != null) {
            return new PolicyConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static PolicyConstraints fromExtensions(Extensions extensions) {
        return PolicyConstraints.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.policyConstraints));
    }

    public BigInteger getRequireExplicitPolicyMapping() {
        return this.requireExplicitPolicyMapping;
    }

    public BigInteger getInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.requireExplicitPolicyMapping != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(this.requireExplicitPolicyMapping)));
        }
        if (this.inhibitPolicyMapping != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)new ASN1Integer(this.inhibitPolicyMapping)));
        }
        return new DERSequence(v);
    }
}

