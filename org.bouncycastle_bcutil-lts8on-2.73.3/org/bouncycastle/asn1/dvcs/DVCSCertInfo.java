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
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
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
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfo
extends ASN1Object {
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

    public DVCSCertInfo(DVCSRequestInformation dvReqInfo, DigestInfo messageImprint, ASN1Integer serialNumber, DVCSTime responseTime) {
        this.dvReqInfo = dvReqInfo;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }

    private DVCSCertInfo(ASN1Sequence seq) {
        int i = 0;
        ASN1Encodable x = seq.getObjectAt(i++);
        try {
            ASN1Integer encVersion = ASN1Integer.getInstance((Object)x);
            this.version = encVersion.intValueExact();
            x = seq.getObjectAt(i++);
        }
        catch (IllegalArgumentException encVersion) {
            // empty catch block
        }
        this.dvReqInfo = DVCSRequestInformation.getInstance(x);
        x = seq.getObjectAt(i++);
        this.messageImprint = DigestInfo.getInstance((Object)x);
        x = seq.getObjectAt(i++);
        this.serialNumber = ASN1Integer.getInstance((Object)x);
        x = seq.getObjectAt(i++);
        this.responseTime = DVCSTime.getInstance(x);
        block10: while (i < seq.size()) {
            if ((x = seq.getObjectAt(i++)) instanceof ASN1TaggedObject) {
                ASN1TaggedObject t = ASN1TaggedObject.getInstance((Object)x);
                int tagNo = t.getTagNo();
                switch (tagNo) {
                    case 0: {
                        this.dvStatus = PKIStatusInfo.getInstance(t, false);
                        continue block10;
                    }
                    case 1: {
                        this.policy = PolicyInformation.getInstance((Object)ASN1Sequence.getInstance((ASN1TaggedObject)t, (boolean)false));
                        continue block10;
                    }
                    case 2: {
                        this.reqSignature = ASN1Set.getInstance((ASN1TaggedObject)t, (boolean)false);
                        continue block10;
                    }
                    case 3: {
                        this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)t, (boolean)false);
                        continue block10;
                    }
                }
                throw new IllegalArgumentException("Unknown tag encountered: " + tagNo);
            }
            try {
                this.extensions = Extensions.getInstance((Object)x);
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
    }

    public static DVCSCertInfo getInstance(Object obj) {
        if (obj instanceof DVCSCertInfo) {
            return (DVCSCertInfo)((Object)obj);
        }
        if (obj != null) {
            return new DVCSCertInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static DVCSCertInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DVCSCertInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
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
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("DVCSCertInfo {\n");
        if (this.version != 1) {
            s.append("version: " + this.version + "\n");
        }
        s.append("dvReqInfo: " + (Object)((Object)this.dvReqInfo) + "\n");
        s.append("messageImprint: " + this.messageImprint + "\n");
        s.append("serialNumber: " + this.serialNumber + "\n");
        s.append("responseTime: " + (Object)((Object)this.responseTime) + "\n");
        if (this.dvStatus != null) {
            s.append("dvStatus: " + (Object)((Object)this.dvStatus) + "\n");
        }
        if (this.policy != null) {
            s.append("policy: " + this.policy + "\n");
        }
        if (this.reqSignature != null) {
            s.append("reqSignature: " + this.reqSignature + "\n");
        }
        if (this.certs != null) {
            s.append("certs: " + this.certs + "\n");
        }
        if (this.extensions != null) {
            s.append("extensions: " + this.extensions + "\n");
        }
        s.append("}\n");
        return s.toString();
    }

    public int getVersion() {
        return this.version;
    }

    private void setVersion(int version) {
        this.version = version;
    }

    public DVCSRequestInformation getDvReqInfo() {
        return this.dvReqInfo;
    }

    private void setDvReqInfo(DVCSRequestInformation dvReqInfo) {
        this.dvReqInfo = dvReqInfo;
    }

    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }

    private void setMessageImprint(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public DVCSTime getResponseTime() {
        return this.responseTime;
    }

    public PKIStatusInfo getDvStatus() {
        return this.dvStatus;
    }

    public PolicyInformation getPolicy() {
        return this.policy;
    }

    public ASN1Set getReqSignature() {
        return this.reqSignature;
    }

    public TargetEtcChain[] getCerts() {
        if (this.certs != null) {
            return TargetEtcChain.arrayFromSequence(this.certs);
        }
        return null;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }
}

