/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERUTF8String
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.cmc.CMCFailInfo;
import org.bouncycastle.asn1.cmc.CMCStatus;
import org.bouncycastle.asn1.cmc.CMCStatusInfoV2;
import org.bouncycastle.asn1.cmc.ExtendedFailInfo;
import org.bouncycastle.asn1.cmc.OtherStatusInfo;
import org.bouncycastle.asn1.cmc.PendInfo;

public class CMCStatusInfoV2Builder {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private ASN1UTF8String statusString;
    private OtherStatusInfo otherInfo;

    public CMCStatusInfoV2Builder(CMCStatus cMCStatus, BodyPartID bodyPartID) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence((ASN1Encodable)bodyPartID);
    }

    public CMCStatusInfoV2Builder(CMCStatus cMCStatus, BodyPartID[] bodyList) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence((ASN1Encodable[])bodyList);
    }

    public CMCStatusInfoV2Builder setStatusString(String statusString) {
        this.statusString = new DERUTF8String(statusString);
        return this;
    }

    public CMCStatusInfoV2Builder setOtherInfo(CMCFailInfo failInfo) {
        this.otherInfo = new OtherStatusInfo(failInfo);
        return this;
    }

    public CMCStatusInfoV2Builder setOtherInfo(ExtendedFailInfo extendedFailInfo) {
        this.otherInfo = new OtherStatusInfo(extendedFailInfo);
        return this;
    }

    public CMCStatusInfoV2Builder setOtherInfo(PendInfo pendInfo) {
        this.otherInfo = new OtherStatusInfo(pendInfo);
        return this;
    }

    public CMCStatusInfoV2 build() {
        return new CMCStatusInfoV2(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
    }
}

