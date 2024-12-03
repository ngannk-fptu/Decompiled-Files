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
import org.bouncycastle.asn1.esf.CrlListID;
import org.bouncycastle.asn1.esf.OcspListID;
import org.bouncycastle.asn1.esf.OtherRevRefs;

public class CrlOcspRef
extends ASN1Object {
    private CrlListID crlids;
    private OcspListID ocspids;
    private OtherRevRefs otherRev;

    public static CrlOcspRef getInstance(Object obj) {
        if (obj instanceof CrlOcspRef) {
            return (CrlOcspRef)((Object)obj);
        }
        if (obj != null) {
            return new CrlOcspRef(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CrlOcspRef(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement(), (int)128);
            switch (o.getTagNo()) {
                case 0: {
                    this.crlids = CrlListID.getInstance(o.getExplicitBaseObject());
                    continue block5;
                }
                case 1: {
                    this.ocspids = OcspListID.getInstance(o.getExplicitBaseObject());
                    continue block5;
                }
                case 2: {
                    this.otherRev = OtherRevRefs.getInstance(o.getExplicitBaseObject());
                    continue block5;
                }
            }
            throw new IllegalArgumentException("illegal tag");
        }
    }

    public CrlOcspRef(CrlListID crlids, OcspListID ocspids, OtherRevRefs otherRev) {
        this.crlids = crlids;
        this.ocspids = ocspids;
        this.otherRev = otherRev;
    }

    public CrlListID getCrlids() {
        return this.crlids;
    }

    public OcspListID getOcspids() {
        return this.ocspids;
    }

    public OtherRevRefs getOtherRev() {
        return this.otherRev;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (null != this.crlids) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.crlids.toASN1Primitive()));
        }
        if (null != this.ocspids) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ocspids.toASN1Primitive()));
        }
        if (null != this.otherRev) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.otherRev.toASN1Primitive()));
        }
        return new DERSequence(v);
    }
}

