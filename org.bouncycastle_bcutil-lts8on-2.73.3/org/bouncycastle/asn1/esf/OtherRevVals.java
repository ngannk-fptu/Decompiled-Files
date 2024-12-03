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

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevVals
extends ASN1Object {
    private ASN1ObjectIdentifier otherRevValType;
    private ASN1Encodable otherRevVals;

    public static OtherRevVals getInstance(Object obj) {
        if (obj instanceof OtherRevVals) {
            return (OtherRevVals)((Object)obj);
        }
        if (obj != null) {
            return new OtherRevVals(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private OtherRevVals(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.otherRevValType = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        try {
            this.otherRevVals = ASN1Primitive.fromByteArray((byte[])seq.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
        }
        catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public OtherRevVals(ASN1ObjectIdentifier otherRevValType, ASN1Encodable otherRevVals) {
        this.otherRevValType = otherRevValType;
        this.otherRevVals = otherRevVals;
    }

    public ASN1ObjectIdentifier getOtherRevValType() {
        return this.otherRevValType;
    }

    public ASN1Encodable getOtherRevVals() {
        return this.otherRevVals;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.otherRevValType);
        v.add(this.otherRevVals);
        return new DERSequence(v);
    }
}

