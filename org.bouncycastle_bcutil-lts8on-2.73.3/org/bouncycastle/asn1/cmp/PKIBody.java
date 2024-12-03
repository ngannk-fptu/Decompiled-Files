/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.pkcs.CertificationRequest
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CAKeyUpdAnnContent;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CRLAnnContent;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.ErrorMsgContent;
import org.bouncycastle.asn1.cmp.GenMsgContent;
import org.bouncycastle.asn1.cmp.GenRepContent;
import org.bouncycastle.asn1.cmp.KeyRecRepContent;
import org.bouncycastle.asn1.cmp.PKIConfirmContent;
import org.bouncycastle.asn1.cmp.PKIMessages;
import org.bouncycastle.asn1.cmp.POPODecKeyChallContent;
import org.bouncycastle.asn1.cmp.POPODecKeyRespContent;
import org.bouncycastle.asn1.cmp.PollRepContent;
import org.bouncycastle.asn1.cmp.PollReqContent;
import org.bouncycastle.asn1.cmp.RevAnnContent;
import org.bouncycastle.asn1.cmp.RevRepContent;
import org.bouncycastle.asn1.cmp.RevReqContent;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.pkcs.CertificationRequest;

public class PKIBody
extends ASN1Object
implements ASN1Choice {
    public static final int TYPE_INIT_REQ = 0;
    public static final int TYPE_INIT_REP = 1;
    public static final int TYPE_CERT_REQ = 2;
    public static final int TYPE_CERT_REP = 3;
    public static final int TYPE_P10_CERT_REQ = 4;
    public static final int TYPE_POPO_CHALL = 5;
    public static final int TYPE_POPO_REP = 6;
    public static final int TYPE_KEY_UPDATE_REQ = 7;
    public static final int TYPE_KEY_UPDATE_REP = 8;
    public static final int TYPE_KEY_RECOVERY_REQ = 9;
    public static final int TYPE_KEY_RECOVERY_REP = 10;
    public static final int TYPE_REVOCATION_REQ = 11;
    public static final int TYPE_REVOCATION_REP = 12;
    public static final int TYPE_CROSS_CERT_REQ = 13;
    public static final int TYPE_CROSS_CERT_REP = 14;
    public static final int TYPE_CA_KEY_UPDATE_ANN = 15;
    public static final int TYPE_CERT_ANN = 16;
    public static final int TYPE_REVOCATION_ANN = 17;
    public static final int TYPE_CRL_ANN = 18;
    public static final int TYPE_CONFIRM = 19;
    public static final int TYPE_NESTED = 20;
    public static final int TYPE_GEN_MSG = 21;
    public static final int TYPE_GEN_REP = 22;
    public static final int TYPE_ERROR = 23;
    public static final int TYPE_CERT_CONFIRM = 24;
    public static final int TYPE_POLL_REQ = 25;
    public static final int TYPE_POLL_REP = 26;
    private final int tagNo;
    private final ASN1Encodable body;

    private PKIBody(ASN1TaggedObject tagged) {
        this.tagNo = tagged.getTagNo();
        this.body = PKIBody.getBodyForType(this.tagNo, (ASN1Encodable)tagged.getExplicitBaseObject());
    }

    public PKIBody(int type, ASN1Encodable content) {
        this.tagNo = type;
        this.body = PKIBody.getBodyForType(type, content);
    }

    public static PKIBody getInstance(Object o) {
        if (o == null || o instanceof PKIBody) {
            return (PKIBody)((Object)o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new PKIBody((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    private static ASN1Encodable getBodyForType(int type, ASN1Encodable o) {
        switch (type) {
            case 0: {
                return CertReqMessages.getInstance(o);
            }
            case 1: {
                return CertRepMessage.getInstance(o);
            }
            case 2: {
                return CertReqMessages.getInstance(o);
            }
            case 3: {
                return CertRepMessage.getInstance(o);
            }
            case 4: {
                return CertificationRequest.getInstance((Object)o);
            }
            case 5: {
                return POPODecKeyChallContent.getInstance(o);
            }
            case 6: {
                return POPODecKeyRespContent.getInstance(o);
            }
            case 7: {
                return CertReqMessages.getInstance(o);
            }
            case 8: {
                return CertRepMessage.getInstance(o);
            }
            case 9: {
                return CertReqMessages.getInstance(o);
            }
            case 10: {
                return KeyRecRepContent.getInstance(o);
            }
            case 11: {
                return RevReqContent.getInstance(o);
            }
            case 12: {
                return RevRepContent.getInstance(o);
            }
            case 13: {
                return CertReqMessages.getInstance(o);
            }
            case 14: {
                return CertRepMessage.getInstance(o);
            }
            case 15: {
                return CAKeyUpdAnnContent.getInstance(o);
            }
            case 16: {
                return CMPCertificate.getInstance(o);
            }
            case 17: {
                return RevAnnContent.getInstance(o);
            }
            case 18: {
                return CRLAnnContent.getInstance(o);
            }
            case 19: {
                return PKIConfirmContent.getInstance(o);
            }
            case 20: {
                return PKIMessages.getInstance(o);
            }
            case 21: {
                return GenMsgContent.getInstance(o);
            }
            case 22: {
                return GenRepContent.getInstance(o);
            }
            case 23: {
                return ErrorMsgContent.getInstance(o);
            }
            case 24: {
                return CertConfirmContent.getInstance(o);
            }
            case 25: {
                return PollReqContent.getInstance(o);
            }
            case 26: {
                return PollRepContent.getInstance(o);
            }
        }
        throw new IllegalArgumentException("unknown tag number: " + type);
    }

    public int getType() {
        return this.tagNo;
    }

    public ASN1Encodable getContent() {
        return this.body;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(true, this.tagNo, this.body);
    }
}

