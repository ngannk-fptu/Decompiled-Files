/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

public class SignaturePolicyId
extends ASN1Object {
    private ASN1ObjectIdentifier sigPolicyId;
    private OtherHashAlgAndValue sigPolicyHash;
    private SigPolicyQualifiers sigPolicyQualifiers;

    public static SignaturePolicyId getInstance(Object obj) {
        if (obj instanceof SignaturePolicyId) {
            return (SignaturePolicyId)((Object)obj);
        }
        if (obj != null) {
            return new SignaturePolicyId(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private SignaturePolicyId(ASN1Sequence seq) {
        if (seq.size() != 2 && seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.sigPolicyId = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.sigPolicyHash = OtherHashAlgAndValue.getInstance(seq.getObjectAt(1));
        if (seq.size() == 3) {
            this.sigPolicyQualifiers = SigPolicyQualifiers.getInstance(seq.getObjectAt(2));
        }
    }

    public SignaturePolicyId(ASN1ObjectIdentifier sigPolicyIdentifier, OtherHashAlgAndValue sigPolicyHash) {
        this(sigPolicyIdentifier, sigPolicyHash, null);
    }

    public SignaturePolicyId(ASN1ObjectIdentifier sigPolicyId, OtherHashAlgAndValue sigPolicyHash, SigPolicyQualifiers sigPolicyQualifiers) {
        this.sigPolicyId = sigPolicyId;
        this.sigPolicyHash = sigPolicyHash;
        this.sigPolicyQualifiers = sigPolicyQualifiers;
    }

    public ASN1ObjectIdentifier getSigPolicyId() {
        return new ASN1ObjectIdentifier(this.sigPolicyId.getId());
    }

    public OtherHashAlgAndValue getSigPolicyHash() {
        return this.sigPolicyHash;
    }

    public SigPolicyQualifiers getSigPolicyQualifiers() {
        return this.sigPolicyQualifiers;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.sigPolicyId);
        v.add((ASN1Encodable)this.sigPolicyHash);
        if (this.sigPolicyQualifiers != null) {
            v.add((ASN1Encodable)this.sigPolicyQualifiers);
        }
        return new DERSequence(v);
    }
}

