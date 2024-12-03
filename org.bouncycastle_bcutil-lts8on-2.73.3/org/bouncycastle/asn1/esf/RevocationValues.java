/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.ocsp.BasicOCSPResponse
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.esf.OtherRevVals;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevocationValues
extends ASN1Object {
    private ASN1Sequence crlVals;
    private ASN1Sequence ocspVals;
    private OtherRevVals otherRevVals;

    public static RevocationValues getInstance(Object obj) {
        if (obj instanceof RevocationValues) {
            return (RevocationValues)((Object)obj);
        }
        if (obj != null) {
            return new RevocationValues(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private RevocationValues(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement(), (int)128);
            switch (o.getTagNo()) {
                case 0: {
                    ASN1Sequence crlValsSeq = (ASN1Sequence)o.getExplicitBaseObject();
                    Enumeration crlValsEnum = crlValsSeq.getObjects();
                    while (crlValsEnum.hasMoreElements()) {
                        CertificateList.getInstance(crlValsEnum.nextElement());
                    }
                    this.crlVals = crlValsSeq;
                    continue block5;
                }
                case 1: {
                    ASN1Sequence ocspValsSeq = (ASN1Sequence)o.getExplicitBaseObject();
                    Enumeration ocspValsEnum = ocspValsSeq.getObjects();
                    while (ocspValsEnum.hasMoreElements()) {
                        BasicOCSPResponse.getInstance(ocspValsEnum.nextElement());
                    }
                    this.ocspVals = ocspValsSeq;
                    continue block5;
                }
                case 2: {
                    this.otherRevVals = OtherRevVals.getInstance(o.getExplicitBaseObject());
                    continue block5;
                }
            }
            throw new IllegalArgumentException("invalid tag: " + o.getTagNo());
        }
    }

    public RevocationValues(CertificateList[] crlVals, BasicOCSPResponse[] ocspVals, OtherRevVals otherRevVals) {
        if (null != crlVals) {
            this.crlVals = new DERSequence((ASN1Encodable[])crlVals);
        }
        if (null != ocspVals) {
            this.ocspVals = new DERSequence((ASN1Encodable[])ocspVals);
        }
        this.otherRevVals = otherRevVals;
    }

    public CertificateList[] getCrlVals() {
        if (null == this.crlVals) {
            return new CertificateList[0];
        }
        CertificateList[] result = new CertificateList[this.crlVals.size()];
        for (int idx = 0; idx < result.length; ++idx) {
            result[idx] = CertificateList.getInstance((Object)this.crlVals.getObjectAt(idx));
        }
        return result;
    }

    public BasicOCSPResponse[] getOcspVals() {
        if (null == this.ocspVals) {
            return new BasicOCSPResponse[0];
        }
        BasicOCSPResponse[] result = new BasicOCSPResponse[this.ocspVals.size()];
        for (int idx = 0; idx < result.length; ++idx) {
            result[idx] = BasicOCSPResponse.getInstance((Object)this.ocspVals.getObjectAt(idx));
        }
        return result;
    }

    public OtherRevVals getOtherRevVals() {
        return this.otherRevVals;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (null != this.crlVals) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.crlVals));
        }
        if (null != this.ocspVals) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ocspVals));
        }
        if (null != this.otherRevVals) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.otherRevVals.toASN1Primitive()));
        }
        return new DERSequence(v);
    }
}

