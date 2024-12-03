/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.ocsp.CertID
 *  org.bouncycastle.asn1.ocsp.CertStatus
 *  org.bouncycastle.asn1.ocsp.OCSPResponse
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.CertificateList
 *  org.bouncycastle.asn1.x509.Extension
 */
package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.smime.SMIMECapabilities;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;

public class CertEtcToken
extends ASN1Object
implements ASN1Choice {
    public static final int TAG_CERTIFICATE = 0;
    public static final int TAG_ESSCERTID = 1;
    public static final int TAG_PKISTATUS = 2;
    public static final int TAG_ASSERTION = 3;
    public static final int TAG_CRL = 4;
    public static final int TAG_OCSPCERTSTATUS = 5;
    public static final int TAG_OCSPCERTID = 6;
    public static final int TAG_OCSPRESPONSE = 7;
    public static final int TAG_CAPABILITIES = 8;
    private static final boolean[] explicit = new boolean[]{false, true, false, true, false, true, false, false, true};
    private int tagNo;
    private ASN1Encodable value;
    private Extension extension;

    public CertEtcToken(int tagNo, ASN1Encodable value) {
        this.tagNo = tagNo;
        this.value = value;
    }

    public CertEtcToken(Extension extension) {
        this.tagNo = -1;
        this.extension = extension;
    }

    private CertEtcToken(ASN1TaggedObject choice) {
        this.tagNo = choice.getTagNo();
        switch (this.tagNo) {
            case 0: {
                this.value = Certificate.getInstance((ASN1TaggedObject)choice, (boolean)false);
                break;
            }
            case 1: {
                this.value = ESSCertID.getInstance(choice.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.value = PKIStatusInfo.getInstance(choice, false);
                break;
            }
            case 3: {
                this.value = ContentInfo.getInstance(choice.getExplicitBaseObject());
                break;
            }
            case 4: {
                this.value = CertificateList.getInstance((ASN1TaggedObject)choice, (boolean)false);
                break;
            }
            case 5: {
                this.value = CertStatus.getInstance((Object)choice.getExplicitBaseObject());
                break;
            }
            case 6: {
                this.value = CertID.getInstance((ASN1TaggedObject)choice, (boolean)false);
                break;
            }
            case 7: {
                this.value = OCSPResponse.getInstance((ASN1TaggedObject)choice, (boolean)false);
                break;
            }
            case 8: {
                this.value = SMIMECapabilities.getInstance(choice.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag: " + this.tagNo);
            }
        }
    }

    public static CertEtcToken getInstance(Object obj) {
        if (obj instanceof CertEtcToken) {
            return (CertEtcToken)((Object)obj);
        }
        if (obj instanceof ASN1TaggedObject) {
            return new CertEtcToken(ASN1TaggedObject.getInstance((Object)obj, (int)128));
        }
        if (obj != null) {
            return new CertEtcToken(Extension.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.extension == null) {
            return new DERTaggedObject(explicit[this.tagNo], this.tagNo, this.value);
        }
        return this.extension.toASN1Primitive();
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    public Extension getExtension() {
        return this.extension;
    }

    public String toString() {
        return "CertEtcToken {\n" + this.value + "}\n";
    }

    public static CertEtcToken[] arrayFromSequence(ASN1Sequence seq) {
        CertEtcToken[] tmp = new CertEtcToken[seq.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = CertEtcToken.getInstance(seq.getObjectAt(i));
        }
        return tmp;
    }
}

