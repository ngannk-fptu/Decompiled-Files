/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class PKIResponse
extends ASN1Object {
    private final ASN1Sequence controlSequence;
    private final ASN1Sequence cmsSequence;
    private final ASN1Sequence otherMsgSequence;

    private PKIResponse(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.controlSequence = ASN1Sequence.getInstance((Object)seq.getObjectAt(0));
        this.cmsSequence = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        this.otherMsgSequence = ASN1Sequence.getInstance((Object)seq.getObjectAt(2));
    }

    public static PKIResponse getInstance(Object o) {
        if (o instanceof PKIResponse) {
            return (PKIResponse)((Object)o);
        }
        if (o != null) {
            return new PKIResponse(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static PKIResponse getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PKIResponse.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.controlSequence);
        v.add((ASN1Encodable)this.cmsSequence);
        v.add((ASN1Encodable)this.otherMsgSequence);
        return new DERSequence(v);
    }

    public ASN1Sequence getControlSequence() {
        return this.controlSequence;
    }

    public ASN1Sequence getCmsSequence() {
        return this.cmsSequence;
    }

    public ASN1Sequence getOtherMsgSequence() {
        return this.otherMsgSequence;
    }
}

