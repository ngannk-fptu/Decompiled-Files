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
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;

public class OtherMsg
extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final ASN1ObjectIdentifier otherMsgType;
    private final ASN1Encodable otherMsgValue;

    public OtherMsg(BodyPartID bodyPartID, ASN1ObjectIdentifier otherMsgType, ASN1Encodable otherMsgValue) {
        this.bodyPartID = bodyPartID;
        this.otherMsgType = otherMsgType;
        this.otherMsgValue = otherMsgValue;
    }

    private OtherMsg(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.otherMsgType = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.otherMsgValue = seq.getObjectAt(2);
    }

    public static OtherMsg getInstance(Object o) {
        if (o instanceof OtherMsg) {
            return (OtherMsg)((Object)o);
        }
        if (o != null) {
            return new OtherMsg(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static OtherMsg getInstance(ASN1TaggedObject obj, boolean explicit) {
        return OtherMsg.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.bodyPartID);
        v.add((ASN1Encodable)this.otherMsgType);
        v.add(this.otherMsgValue);
        return new DERSequence(v);
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public ASN1ObjectIdentifier getOtherMsgType() {
        return this.otherMsgType;
    }

    public ASN1Encodable getOtherMsgValue() {
        return this.otherMsgValue;
    }
}

