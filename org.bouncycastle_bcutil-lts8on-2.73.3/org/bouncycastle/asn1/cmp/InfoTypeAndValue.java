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
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class InfoTypeAndValue
extends ASN1Object {
    private final ASN1ObjectIdentifier infoType;
    private final ASN1Encodable infoValue;

    private InfoTypeAndValue(ASN1Sequence seq) {
        this.infoType = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.infoValue = seq.size() > 1 ? seq.getObjectAt(1) : null;
    }

    public InfoTypeAndValue(ASN1ObjectIdentifier infoType) {
        this(infoType, null);
    }

    public InfoTypeAndValue(ASN1ObjectIdentifier infoType, ASN1Encodable infoValue) {
        if (infoType == null) {
            throw new NullPointerException("'infoType' cannot be null");
        }
        this.infoType = infoType;
        this.infoValue = infoValue;
    }

    public static InfoTypeAndValue getInstance(Object o) {
        if (o instanceof InfoTypeAndValue) {
            return (InfoTypeAndValue)((Object)o);
        }
        if (o != null) {
            return new InfoTypeAndValue(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1ObjectIdentifier getInfoType() {
        return this.infoType;
    }

    public ASN1Encodable getInfoValue() {
        return this.infoValue;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.infoType);
        if (this.infoValue != null) {
            v.add(this.infoValue);
        }
        return new DERSequence(v);
    }
}

