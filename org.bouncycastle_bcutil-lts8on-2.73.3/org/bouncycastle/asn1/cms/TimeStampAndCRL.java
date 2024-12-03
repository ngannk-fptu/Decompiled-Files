/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.CertificateList;

public class TimeStampAndCRL
extends ASN1Object {
    private ContentInfo timeStamp;
    private CertificateList crl;

    public TimeStampAndCRL(ContentInfo timeStamp) {
        this.timeStamp = timeStamp;
    }

    private TimeStampAndCRL(ASN1Sequence seq) {
        this.timeStamp = ContentInfo.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.crl = CertificateList.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public static TimeStampAndCRL getInstance(Object obj) {
        if (obj instanceof TimeStampAndCRL) {
            return (TimeStampAndCRL)((Object)obj);
        }
        if (obj != null) {
            return new TimeStampAndCRL(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ContentInfo getTimeStampToken() {
        return this.timeStamp;
    }

    public CertificateList getCRL() {
        return this.crl;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.timeStamp);
        if (this.crl != null) {
            v.add((ASN1Encodable)this.crl);
        }
        return new DERSequence(v);
    }
}

