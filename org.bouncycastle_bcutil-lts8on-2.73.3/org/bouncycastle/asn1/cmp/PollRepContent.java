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
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIFreeText;

public class PollRepContent
extends ASN1Object {
    private final ASN1Integer[] certReqId;
    private final ASN1Integer[] checkAfter;
    private final PKIFreeText[] reason;

    private PollRepContent(ASN1Sequence seq) {
        this.certReqId = new ASN1Integer[seq.size()];
        this.checkAfter = new ASN1Integer[seq.size()];
        this.reason = new PKIFreeText[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            ASN1Sequence s = ASN1Sequence.getInstance((Object)seq.getObjectAt(i));
            this.certReqId[i] = ASN1Integer.getInstance((Object)s.getObjectAt(0));
            this.checkAfter[i] = ASN1Integer.getInstance((Object)s.getObjectAt(1));
            if (s.size() <= 2) continue;
            this.reason[i] = PKIFreeText.getInstance(s.getObjectAt(2));
        }
    }

    public PollRepContent(ASN1Integer certReqId, ASN1Integer checkAfter) {
        this(certReqId, checkAfter, null);
    }

    public PollRepContent(ASN1Integer certReqId, ASN1Integer checkAfter, PKIFreeText reason) {
        this.certReqId = new ASN1Integer[1];
        this.checkAfter = new ASN1Integer[1];
        this.reason = new PKIFreeText[1];
        this.certReqId[0] = certReqId;
        this.checkAfter[0] = checkAfter;
        this.reason[0] = reason;
    }

    public static PollRepContent getInstance(Object o) {
        if (o instanceof PollRepContent) {
            return (PollRepContent)((Object)o);
        }
        if (o != null) {
            return new PollRepContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public int size() {
        return this.certReqId.length;
    }

    public ASN1Integer getCertReqId(int index) {
        return this.certReqId[index];
    }

    public ASN1Integer getCheckAfter(int index) {
        return this.checkAfter[index];
    }

    public PKIFreeText getReason(int index) {
        return this.reason[index];
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector outer = new ASN1EncodableVector(this.certReqId.length);
        for (int i = 0; i != this.certReqId.length; ++i) {
            ASN1EncodableVector v = new ASN1EncodableVector(3);
            v.add((ASN1Encodable)this.certReqId[i]);
            v.add((ASN1Encodable)this.checkAfter[i]);
            if (this.reason[i] != null) {
                v.add((ASN1Encodable)this.reason[i]);
            }
            outer.add((ASN1Encodable)new DERSequence(v));
        }
        return new DERSequence(outer);
    }
}

