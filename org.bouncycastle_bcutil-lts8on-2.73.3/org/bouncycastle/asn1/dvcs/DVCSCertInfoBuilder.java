/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.DigestInfo
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.PolicyInformation
 */
package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.dvcs.DVCSCertInfo;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfoBuilder {
    private int version = 1;
    private DVCSRequestInformation dvReqInfo;
    private DigestInfo messageImprint;
    private ASN1Integer serialNumber;
    private DVCSTime responseTime;
    private PKIStatusInfo dvStatus;
    private PolicyInformation policy;
    private ASN1Set reqSignature;
    private ASN1Sequence certs;
    private Extensions extensions;
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_DV_STATUS = 0;
    private static final int TAG_POLICY = 1;
    private static final int TAG_REQ_SIGNATURE = 2;
    private static final int TAG_CERTS = 3;

    public DVCSCertInfoBuilder(DVCSRequestInformation dvReqInfo, DigestInfo messageImprint, ASN1Integer serialNumber, DVCSTime responseTime) {
        this.dvReqInfo = dvReqInfo;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }

    public DVCSCertInfo build() {
        ASN1EncodableVector v = new ASN1EncodableVector(10);
        if (this.version != 1) {
            v.add((ASN1Encodable)new ASN1Integer((long)this.version));
        }
        v.add((ASN1Encodable)this.dvReqInfo);
        v.add((ASN1Encodable)this.messageImprint);
        v.add((ASN1Encodable)this.serialNumber);
        v.add((ASN1Encodable)this.responseTime);
        if (this.dvStatus != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.dvStatus));
        }
        if (this.policy != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.policy));
        }
        if (this.reqSignature != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.reqSignature));
        }
        if (this.certs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)this.certs));
        }
        if (this.extensions != null) {
            v.add((ASN1Encodable)this.extensions);
        }
        return DVCSCertInfo.getInstance(new DERSequence(v));
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setDvReqInfo(DVCSRequestInformation dvReqInfo) {
        this.dvReqInfo = dvReqInfo;
    }

    public void setMessageImprint(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setResponseTime(DVCSTime responseTime) {
        this.responseTime = responseTime;
    }

    public void setDvStatus(PKIStatusInfo dvStatus) {
        this.dvStatus = dvStatus;
    }

    public void setPolicy(PolicyInformation policy) {
        this.policy = policy;
    }

    public void setReqSignature(ASN1Set reqSignature) {
        this.reqSignature = reqSignature;
    }

    public void setCerts(TargetEtcChain[] certs) {
        this.certs = new DERSequence((ASN1Encodable[])certs);
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }
}

