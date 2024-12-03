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

public class CommitmentTypeIndication
extends ASN1Object {
    private ASN1ObjectIdentifier commitmentTypeId;
    private ASN1Sequence commitmentTypeQualifier;

    private CommitmentTypeIndication(ASN1Sequence seq) {
        this.commitmentTypeId = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        if (seq.size() > 1) {
            this.commitmentTypeQualifier = (ASN1Sequence)seq.getObjectAt(1);
        }
    }

    public CommitmentTypeIndication(ASN1ObjectIdentifier commitmentTypeId) {
        this.commitmentTypeId = commitmentTypeId;
    }

    public CommitmentTypeIndication(ASN1ObjectIdentifier commitmentTypeId, ASN1Sequence commitmentTypeQualifier) {
        this.commitmentTypeId = commitmentTypeId;
        this.commitmentTypeQualifier = commitmentTypeQualifier;
    }

    public static CommitmentTypeIndication getInstance(Object obj) {
        if (obj == null || obj instanceof CommitmentTypeIndication) {
            return (CommitmentTypeIndication)((Object)obj);
        }
        return new CommitmentTypeIndication(ASN1Sequence.getInstance((Object)obj));
    }

    public ASN1ObjectIdentifier getCommitmentTypeId() {
        return this.commitmentTypeId;
    }

    public ASN1Sequence getCommitmentTypeQualifier() {
        return this.commitmentTypeQualifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.commitmentTypeId);
        if (this.commitmentTypeQualifier != null) {
            v.add((ASN1Encodable)this.commitmentTypeQualifier);
        }
        return new DERSequence(v);
    }
}

