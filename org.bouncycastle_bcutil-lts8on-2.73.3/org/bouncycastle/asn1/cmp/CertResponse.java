/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;

public class CertResponse
extends ASN1Object {
    private final ASN1Integer certReqId;
    private final PKIStatusInfo status;
    private CertifiedKeyPair certifiedKeyPair;
    private ASN1OctetString rspInfo;

    private CertResponse(ASN1Sequence seq) {
        this.certReqId = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        this.status = PKIStatusInfo.getInstance(seq.getObjectAt(1));
        if (seq.size() >= 3) {
            if (seq.size() == 3) {
                ASN1Encodable o = seq.getObjectAt(2);
                if (o instanceof ASN1OctetString) {
                    this.rspInfo = ASN1OctetString.getInstance((Object)o);
                } else {
                    this.certifiedKeyPair = CertifiedKeyPair.getInstance(o);
                }
            } else {
                this.certifiedKeyPair = CertifiedKeyPair.getInstance(seq.getObjectAt(2));
                this.rspInfo = ASN1OctetString.getInstance((Object)seq.getObjectAt(3));
            }
        }
    }

    public CertResponse(ASN1Integer certReqId, PKIStatusInfo status) {
        this(certReqId, status, null, null);
    }

    public CertResponse(ASN1Integer certReqId, PKIStatusInfo status, CertifiedKeyPair certifiedKeyPair, ASN1OctetString rspInfo) {
        if (certReqId == null) {
            throw new IllegalArgumentException("'certReqId' cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("'status' cannot be null");
        }
        this.certReqId = certReqId;
        this.status = status;
        this.certifiedKeyPair = certifiedKeyPair;
        this.rspInfo = rspInfo;
    }

    public static CertResponse getInstance(Object o) {
        if (o instanceof CertResponse) {
            return (CertResponse)((Object)o);
        }
        if (o != null) {
            return new CertResponse(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public PKIStatusInfo getStatus() {
        return this.status;
    }

    public CertifiedKeyPair getCertifiedKeyPair() {
        return this.certifiedKeyPair;
    }

    public ASN1OctetString getRspInfo() {
        return this.rspInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.certReqId);
        v.add((ASN1Encodable)this.status);
        if (this.certifiedKeyPair != null) {
            v.add((ASN1Encodable)this.certifiedKeyPair);
        }
        if (this.rspInfo != null) {
            v.add((ASN1Encodable)this.rspInfo);
        }
        return new DERSequence(v);
    }
}

