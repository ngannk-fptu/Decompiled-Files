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

public class CommitmentTypeQualifier
extends ASN1Object {
    private ASN1ObjectIdentifier commitmentTypeIdentifier;
    private ASN1Encodable qualifier;

    public CommitmentTypeQualifier(ASN1ObjectIdentifier commitmentTypeIdentifier) {
        this(commitmentTypeIdentifier, null);
    }

    public CommitmentTypeQualifier(ASN1ObjectIdentifier commitmentTypeIdentifier, ASN1Encodable qualifier) {
        this.commitmentTypeIdentifier = commitmentTypeIdentifier;
        this.qualifier = qualifier;
    }

    private CommitmentTypeQualifier(ASN1Sequence as) {
        this.commitmentTypeIdentifier = (ASN1ObjectIdentifier)as.getObjectAt(0);
        if (as.size() > 1) {
            this.qualifier = as.getObjectAt(1);
        }
    }

    public static CommitmentTypeQualifier getInstance(Object as) {
        if (as instanceof CommitmentTypeQualifier) {
            return (CommitmentTypeQualifier)((Object)as);
        }
        if (as != null) {
            return new CommitmentTypeQualifier(ASN1Sequence.getInstance((Object)as));
        }
        return null;
    }

    public ASN1ObjectIdentifier getCommitmentTypeIdentifier() {
        return this.commitmentTypeIdentifier;
    }

    public ASN1Encodable getQualifier() {
        return this.qualifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector dev = new ASN1EncodableVector(2);
        dev.add((ASN1Encodable)this.commitmentTypeIdentifier);
        if (this.qualifier != null) {
            dev.add(this.qualifier);
        }
        return new DERSequence(dev);
    }
}

