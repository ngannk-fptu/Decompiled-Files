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
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.ContentInfo;

public class SCVPReqRes
extends ASN1Object {
    private final ContentInfo request;
    private final ContentInfo response;

    public static SCVPReqRes getInstance(Object obj) {
        if (obj instanceof SCVPReqRes) {
            return (SCVPReqRes)((Object)obj);
        }
        if (obj != null) {
            return new SCVPReqRes(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private SCVPReqRes(ASN1Sequence seq) {
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.request = ContentInfo.getInstance(ASN1TaggedObject.getInstance((Object)seq.getObjectAt(0)), true);
            this.response = ContentInfo.getInstance(seq.getObjectAt(1));
        } else {
            this.request = null;
            this.response = ContentInfo.getInstance(seq.getObjectAt(0));
        }
    }

    public SCVPReqRes(ContentInfo response) {
        this.request = null;
        this.response = response;
    }

    public SCVPReqRes(ContentInfo request, ContentInfo response) {
        this.request = request;
        this.response = response;
    }

    public ContentInfo getRequest() {
        return this.request;
    }

    public ContentInfo getResponse() {
        return this.response;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.request != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.request));
        }
        v.add((ASN1Encodable)this.response);
        return new DERSequence(v);
    }
}

