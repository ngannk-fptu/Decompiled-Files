/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x9.KeySpecificInfo;

public class OtherInfo
extends ASN1Object {
    private KeySpecificInfo keyInfo;
    private ASN1OctetString partyAInfo;
    private ASN1OctetString suppPubInfo;

    public OtherInfo(KeySpecificInfo keyInfo, ASN1OctetString partyAInfo, ASN1OctetString suppPubInfo) {
        this.keyInfo = keyInfo;
        this.partyAInfo = partyAInfo;
        this.suppPubInfo = suppPubInfo;
    }

    public static OtherInfo getInstance(Object obj) {
        if (obj instanceof OtherInfo) {
            return (OtherInfo)obj;
        }
        if (obj != null) {
            return new OtherInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private OtherInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.keyInfo = KeySpecificInfo.getInstance(e.nextElement());
        while (e.hasMoreElements()) {
            ASN1TaggedObject o = (ASN1TaggedObject)e.nextElement();
            if (o.hasContextTag(0)) {
                this.partyAInfo = (ASN1OctetString)o.getExplicitBaseObject();
                continue;
            }
            if (!o.hasContextTag(2)) continue;
            this.suppPubInfo = (ASN1OctetString)o.getExplicitBaseObject();
        }
    }

    public KeySpecificInfo getKeyInfo() {
        return this.keyInfo;
    }

    public ASN1OctetString getPartyAInfo() {
        return this.partyAInfo;
    }

    public ASN1OctetString getSuppPubInfo() {
        return this.suppPubInfo;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.keyInfo);
        if (this.partyAInfo != null) {
            v.add(new DERTaggedObject(0, this.partyAInfo));
        }
        v.add(new DERTaggedObject(2, this.suppPubInfo));
        return new DERSequence(v);
    }
}

