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
import org.bouncycastle.asn1.cmc.CMCStatusInfo;
import org.bouncycastle.asn1.cmc.PendInfo;

public class CMCStatusInfoBuilder {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private ASN1UTF8String statusString;
    private CMCStatusInfo.OtherInfo otherInfo;

    public CMCStatusInfoBuilder(CMCStatus cMCStatus, BodyPartID bodyPartID) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence((ASN1Encodable)bodyPartID);
    }

    public CMCStatusInfoBuilder(CMCStatus cMCStatus, BodyPartID[] bodyList) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence((ASN1Encodable[])bodyList);
    }

    public CMCStatusInfoBuilder setStatusString(String statusString) {
        this.statusString = new DERUTF8String(statusString);
        return this;
    }

    public CMCStatusInfoBuilder setOtherInfo(CMCFailInfo failInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(failInfo);
        return this;
    }

    public CMCStatusInfoBuilder setOtherInfo(PendInfo pendInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(pendInfo);
        return this;
    }

    public CMCStatusInfo build() {
        return new CMCStatusInfo(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
    }
}

