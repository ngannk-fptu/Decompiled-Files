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
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevocationInfoFormat
extends ASN1Object {
    private ASN1ObjectIdentifier otherRevInfoFormat;
    private ASN1Encodable otherRevInfo;

    public OtherRevocationInfoFormat(ASN1ObjectIdentifier otherRevInfoFormat, ASN1Encodable otherRevInfo) {
        this.otherRevInfoFormat = otherRevInfoFormat;
        this.otherRevInfo = otherRevInfo;
    }

    private OtherRevocationInfoFormat(ASN1Sequence seq) {
        this.otherRevInfoFormat = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.otherRevInfo = seq.getObjectAt(1);
    }

    public static OtherRevocationInfoFormat getInstance(ASN1TaggedObject obj, boolean explicit) {
        return OtherRevocationInfoFormat.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static OtherRevocationInfoFormat getInstance(Object obj) {
        if (obj instanceof OtherRevocationInfoFormat) {
            return (OtherRevocationInfoFormat)((Object)obj);
        }
        if (obj != null) {
            return new OtherRevocationInfoFormat(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getInfoFormat() {
        return this.otherRevInfoFormat;
    }

    public ASN1Encodable getInfo() {
        return this.otherRevInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.otherRevInfoFormat);
        v.add(this.otherRevInfo);
        return new DERSequence(v);
    }
}

