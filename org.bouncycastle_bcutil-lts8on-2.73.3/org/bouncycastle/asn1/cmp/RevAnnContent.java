/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatus;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.Extensions;

public class RevAnnContent
extends ASN1Object {
    private final PKIStatus status;
    private final CertId certId;
    private final ASN1GeneralizedTime willBeRevokedAt;
    private final ASN1GeneralizedTime badSinceDate;
    private Extensions crlDetails;

    public RevAnnContent(PKIStatus status, CertId certId, ASN1GeneralizedTime willBeRevokedAt, ASN1GeneralizedTime badSinceDate) {
        this(status, certId, willBeRevokedAt, badSinceDate, null);
    }

    public RevAnnContent(PKIStatus status, CertId certId, ASN1GeneralizedTime willBeRevokedAt, ASN1GeneralizedTime badSinceDate, Extensions crlDetails) {
        this.status = status;
        this.certId = certId;
        this.willBeRevokedAt = willBeRevokedAt;
        this.badSinceDate = badSinceDate;
        this.crlDetails = crlDetails;
    }

    private RevAnnContent(ASN1Sequence seq) {
        this.status = PKIStatus.getInstance(seq.getObjectAt(0));
        this.certId = CertId.getInstance(seq.getObjectAt(1));
        this.willBeRevokedAt = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(2));
        this.badSinceDate = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(3));
        if (seq.size() > 4) {
            this.crlDetails = Extensions.getInstance((Object)seq.getObjectAt(4));
        }
    }

    public static RevAnnContent getInstance(Object o) {
        if (o instanceof RevAnnContent) {
            return (RevAnnContent)((Object)o);
        }
        if (o != null) {
            return new RevAnnContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIStatus getStatus() {
        return this.status;
    }

    public CertId getCertId() {
        return this.certId;
    }

    public ASN1GeneralizedTime getWillBeRevokedAt() {
        return this.willBeRevokedAt;
    }

    public ASN1GeneralizedTime getBadSinceDate() {
        return this.badSinceDate;
    }

    public Extensions getCrlDetails() {
        return this.crlDetails;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add((ASN1Encodable)this.status);
        v.add((ASN1Encodable)this.certId);
        v.add((ASN1Encodable)this.willBeRevokedAt);
        v.add((ASN1Encodable)this.badSinceDate);
        if (this.crlDetails != null) {
            v.add((ASN1Encodable)this.crlDetails);
        }
        return new DERSequence(v);
    }
}

