/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.cmc.CMCFailInfo;
import org.bouncycastle.asn1.cmc.CMCStatus;
import org.bouncycastle.asn1.cmc.PendInfo;
import org.bouncycastle.asn1.cmc.Utils;

public class CMCStatusInfo
extends ASN1Object {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final ASN1UTF8String statusString;
    private final OtherInfo otherInfo;

    CMCStatusInfo(CMCStatus cMCStatus, ASN1Sequence bodyList, ASN1UTF8String statusString, OtherInfo otherInfo) {
        this.cMCStatus = cMCStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherInfo = otherInfo;
    }

    private CMCStatusInfo(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(seq.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        if (seq.size() > 3) {
            this.statusString = ASN1UTF8String.getInstance((Object)seq.getObjectAt(2));
            this.otherInfo = OtherInfo.getInstance(seq.getObjectAt(3));
        } else if (seq.size() > 2) {
            if (seq.getObjectAt(2) instanceof ASN1UTF8String) {
                this.statusString = ASN1UTF8String.getInstance((Object)seq.getObjectAt(2));
                this.otherInfo = null;
            } else {
                this.statusString = null;
                this.otherInfo = OtherInfo.getInstance(seq.getObjectAt(2));
            }
        } else {
            this.statusString = null;
            this.otherInfo = null;
        }
    }

    public static CMCStatusInfo getInstance(Object o) {
        if (o instanceof CMCStatusInfo) {
            return (CMCStatusInfo)((Object)o);
        }
        if (o != null) {
            return new CMCStatusInfo(ASN1Sequence.getInstance((Object)o));
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
        if (this.otherInfo != null) {
            v.add((ASN1Encodable)this.otherInfo);
        }
        return new DERSequence(v);
    }

    public CMCStatus getCMCStatus() {
        return this.cMCStatus;
    }

    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }

    public ASN1UTF8String getStatusStringUTF8() {
        return this.statusString;
    }

    public boolean hasOtherInfo() {
        return this.otherInfo != null;
    }

    public OtherInfo getOtherInfo() {
        return this.otherInfo;
    }

    public static class OtherInfo
    extends ASN1Object
    implements ASN1Choice {
        private final CMCFailInfo failInfo;
        private final PendInfo pendInfo;

        private static OtherInfo getInstance(Object obj) {
            if (obj instanceof OtherInfo) {
                return (OtherInfo)((Object)obj);
            }
            if (obj instanceof ASN1Encodable) {
                ASN1Primitive asn1Value = ((ASN1Encodable)obj).toASN1Primitive();
                if (asn1Value instanceof ASN1Integer) {
                    return new OtherInfo(CMCFailInfo.getInstance(asn1Value));
                }
                if (asn1Value instanceof ASN1Sequence) {
                    return new OtherInfo(PendInfo.getInstance(asn1Value));
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
        }

        OtherInfo(CMCFailInfo failInfo) {
            this(failInfo, null);
        }

        OtherInfo(PendInfo pendInfo) {
            this(null, pendInfo);
        }

        private OtherInfo(CMCFailInfo failInfo, PendInfo pendInfo) {
            this.failInfo = failInfo;
            this.pendInfo = pendInfo;
        }

        public boolean isFailInfo() {
            return this.failInfo != null;
        }

        public ASN1Primitive toASN1Primitive() {
            if (this.pendInfo != null) {
                return this.pendInfo.toASN1Primitive();
            }
            return this.failInfo.toASN1Primitive();
        }
    }
}

