/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.SinglePubInfo;

public class PKIPublicationInfo
extends ASN1Object {
    public static final ASN1Integer dontPublish = new ASN1Integer(0L);
    public static final ASN1Integer pleasePublish = new ASN1Integer(1L);
    private ASN1Integer action;
    private ASN1Sequence pubInfos;

    private PKIPublicationInfo(ASN1Sequence seq) {
        this.action = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.pubInfos = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public static PKIPublicationInfo getInstance(Object o) {
        if (o instanceof PKIPublicationInfo) {
            return (PKIPublicationInfo)((Object)o);
        }
        if (o != null) {
            return new PKIPublicationInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIPublicationInfo(BigInteger action) {
        this(new ASN1Integer(action));
    }

    public PKIPublicationInfo(ASN1Integer action) {
        this.action = action;
    }

    public PKIPublicationInfo(SinglePubInfo pubInfo) {
        SinglePubInfo[] singlePubInfoArray;
        if (pubInfo != null) {
            SinglePubInfo[] singlePubInfoArray2 = new SinglePubInfo[1];
            singlePubInfoArray = singlePubInfoArray2;
            singlePubInfoArray2[0] = pubInfo;
        } else {
            singlePubInfoArray = null;
        }
        this(singlePubInfoArray);
    }

    public PKIPublicationInfo(SinglePubInfo[] pubInfos) {
        this.action = pleasePublish;
        this.pubInfos = pubInfos != null ? new DERSequence((ASN1Encodable[])pubInfos) : null;
    }

    public ASN1Integer getAction() {
        return this.action;
    }

    public SinglePubInfo[] getPubInfos() {
        if (this.pubInfos == null) {
            return null;
        }
        SinglePubInfo[] results = new SinglePubInfo[this.pubInfos.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.action);
        if (this.pubInfos != null) {
            v.add((ASN1Encodable)this.pubInfos);
        }
        return new DERSequence(v);
    }
}

