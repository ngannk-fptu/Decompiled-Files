/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatus;

public class PKIStatusInfo
extends ASN1Object {
    ASN1Integer status;
    PKIFreeText statusString;
    ASN1BitString failInfo;

    private PKIStatusInfo(ASN1Sequence seq) {
        this.status = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        this.statusString = null;
        this.failInfo = null;
        if (seq.size() > 2) {
            this.statusString = PKIFreeText.getInstance(seq.getObjectAt(1));
            this.failInfo = ASN1BitString.getInstance((Object)seq.getObjectAt(2));
        } else if (seq.size() > 1) {
            ASN1Encodable obj = seq.getObjectAt(1);
            if (obj instanceof ASN1BitString) {
                this.failInfo = ASN1BitString.getInstance((Object)obj);
            } else {
                this.statusString = PKIFreeText.getInstance(obj);
            }
        }
    }

    public PKIStatusInfo(PKIStatus status) {
        this.status = ASN1Integer.getInstance((Object)status.toASN1Primitive());
    }

    public PKIStatusInfo(PKIStatus status, PKIFreeText statusString) {
        this.status = ASN1Integer.getInstance((Object)status.toASN1Primitive());
        this.statusString = statusString;
    }

    public PKIStatusInfo(PKIStatus status, PKIFreeText statusString, PKIFailureInfo failInfo) {
        this.status = ASN1Integer.getInstance((Object)status.toASN1Primitive());
        this.statusString = statusString;
        this.failInfo = failInfo;
    }

    public static PKIStatusInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PKIStatusInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static PKIStatusInfo getInstance(Object obj) {
        if (obj instanceof PKIStatusInfo) {
            return (PKIStatusInfo)((Object)obj);
        }
        if (obj != null) {
            return new PKIStatusInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public BigInteger getStatus() {
        return this.status.getValue();
    }

    public PKIFreeText getStatusString() {
        return this.statusString;
    }

    public ASN1BitString getFailInfo() {
        return this.failInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.status);
        if (this.statusString != null) {
            v.add((ASN1Encodable)this.statusString);
        }
        if (this.failInfo != null) {
            v.add((ASN1Encodable)this.failInfo);
        }
        return new DERSequence(v);
    }
}

