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
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
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

    public static CrlValidatedID getInstance(Object obj) {
        if (obj instanceof CrlValidatedID) {
            return (CrlValidatedID)((Object)obj);
        }
        if (obj != null) {
            return new CrlValidatedID(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CrlValidatedID(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.crlHash = OtherHash.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.crlIdentifier = CrlIdentifier.getInstance(seq.getObjectAt(1));
        }
    }

    public CrlValidatedID(OtherHash crlHash) {
        this(crlHash, null);
    }

    public CrlValidatedID(OtherHash crlHash, CrlIdentifier crlIdentifier) {
        this.crlHash = crlHash;
        this.crlIdentifier = crlIdentifier;
    }

    public OtherHash getCrlHash() {
        return this.crlHash;
    }

    public CrlIdentifier getCrlIdentifier() {
        return this.crlIdentifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.crlHash.toASN1Primitive());
        if (null != this.crlIdentifier) {
            v.add((ASN1Encodable)this.crlIdentifier.toASN1Primitive());
        }
        return new DERSequence(v);
    }
}

