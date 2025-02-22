/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CrlID
extends ASN1Object {
    private ASN1IA5String crlUrl;
    private ASN1Integer crlNum;
    private ASN1GeneralizedTime crlTime;

    private CrlID(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = (ASN1TaggedObject)e.nextElement();
            switch (o.getTagNo()) {
                case 0: {
                    this.crlUrl = ASN1IA5String.getInstance(o, true);
                    continue block5;
                }
                case 1: {
                    this.crlNum = ASN1Integer.getInstance(o, true);
                    continue block5;
                }
                case 2: {
                    this.crlTime = ASN1GeneralizedTime.getInstance(o, true);
                    continue block5;
                }
            }
            throw new IllegalArgumentException("unknown tag number: " + o.getTagNo());
        }
    }

    public static CrlID getInstance(Object obj) {
        if (obj instanceof CrlID) {
            return (CrlID)obj;
        }
        if (obj != null) {
            return new CrlID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1IA5String getCrlUrlIA5() {
        return this.crlUrl;
    }

    public ASN1Integer getCrlNum() {
        return this.crlNum;
    }

    public ASN1GeneralizedTime getCrlTime() {
        return this.crlTime;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.crlUrl != null) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.crlUrl));
        }
        if (this.crlNum != null) {
            v.add(new DERTaggedObject(true, 1, (ASN1Encodable)this.crlNum));
        }
        if (this.crlTime != null) {
            v.add(new DERTaggedObject(true, 2, (ASN1Encodable)this.crlTime));
        }
        return new DERSequence(v);
    }
}

