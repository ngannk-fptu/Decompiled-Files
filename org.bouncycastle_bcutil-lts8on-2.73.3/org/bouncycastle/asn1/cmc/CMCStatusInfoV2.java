/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERUTF8String
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.cmc.CMCStatus;
import org.bouncycastle.asn1.cmc.OtherStatusInfo;
import org.bouncycastle.asn1.cmc.Utils;

public class CMCStatusInfoV2
extends ASN1Object {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final ASN1UTF8String statusString;
    private final OtherStatusInfo otherStatusInfo;

    CMCStatusInfoV2(CMCStatus cMCStatus, ASN1Sequence bodyList, ASN1UTF8String statusString, OtherStatusInfo otherStatusInfo) {
        this.cMCStatus = cMCStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherStatusInfo = otherStatusInfo;
    }

    private CMCStatusInfoV2(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(seq.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        if (seq.size() > 2) {
            if (seq.size() == 4) {
                this.statusString = ASN1UTF8String.getInstance((Object)seq.getObjectAt(2));
                this.otherStatusInfo = OtherStatusInfo.getInstance(seq.getObjectAt(3));
            } else if (seq.getObjectAt(2) instanceof ASN1UTF8String) {
                this.statusString = ASN1UTF8String.getInstance((Object)seq.getObjectAt(2));
                this.otherStatusInfo = null;
            } else {
                this.statusString = null;
                this.otherStatusInfo = OtherStatusInfo.getInstance(seq.getObjectAt(2));
            }
        } else {
            this.statusString = null;
            this.otherStatusInfo = null;
        }
    }

    public CMCStatus getCMCStatus() {
        return this.cMCStatus;
    }

    public CMCStatus getcMCStatus() {
        return this.cMCStatus;
    }

    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }

    public DERUTF8String getStatusString() {
        return null == this.statusString || this.statusString instanceof DERUTF8String ? (DERUTF8String)this.statusString : new DERUTF8String(this.statusString.getString());
    }

    public ASN1UTF8String getStatusStringUTF8() {
        return this.statusString;
    }

    public OtherStatusInfo getOtherStatusInfo() {
        return this.otherStatusInfo;
    }

    public boolean hasOtherInfo() {
        return this.otherStatusInfo != null;
    }

    public static CMCStatusInfoV2 getInstance(Object o) {
        if (o instanceof CMCStatusInfoV2) {
            return (CMCStatusInfoV2)((Object)o);
        }
        if (o != null) {
            return new CMCStatusInfoV2(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.cMCStatus);
        v.add((ASN1Encodable)this.bodyList);
        if (this.statusString != null) {
            v.add((ASN1Encodable)this.statusString);
        }
        if (this.otherStatusInfo != null) {
            v.add((ASN1Encodable)this.otherStatusInfo);
        }
        return new DERSequence(v);
    }
}

