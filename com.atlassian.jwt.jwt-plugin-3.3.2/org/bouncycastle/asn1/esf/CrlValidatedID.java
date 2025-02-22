/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.esf.CrlIdentifier;
import org.bouncycastle.asn1.esf.OtherHash;

public class CrlValidatedID
extends ASN1Object {
    private OtherHash crlHash;
    private CrlIdentifier crlIdentifier;

    public static CrlValidatedID getInstance(Object object) {
        if (object instanceof CrlValidatedID) {
            return (CrlValidatedID)object;
        }
        if (object != null) {
            return new CrlValidatedID(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private CrlValidatedID(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 1 || aSN1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        this.crlHash = OtherHash.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() > 1) {
            this.crlIdentifier = CrlIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public CrlValidatedID(OtherHash otherHash) {
        this(otherHash, null);
    }

    public CrlValidatedID(OtherHash otherHash, CrlIdentifier crlIdentifier) {
        this.crlHash = otherHash;
        this.crlIdentifier = crlIdentifier;
    }

    public OtherHash getCrlHash() {
        return this.crlHash;
    }

    public CrlIdentifier getCrlIdentifier() {
        return this.crlIdentifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.crlHash.toASN1Primitive());
        if (null != this.crlIdentifier) {
            aSN1EncodableVector.add(this.crlIdentifier.toASN1Primitive());
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

