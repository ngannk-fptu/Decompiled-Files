/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OriginatorInfo
extends ASN1Object {
    private ASN1Set certs;
    private ASN1Set crls;

    public OriginatorInfo(ASN1Set certs, ASN1Set crls) {
        this.certs = certs;
        this.crls = crls;
    }

    private OriginatorInfo(ASN1Sequence seq) {
        block0 : switch (seq.size()) {
            case 0: {
                break;
            }
            case 1: {
                ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(0);
                switch (o.getTagNo()) {
                    case 0: {
                        this.certs = ASN1Set.getInstance((ASN1TaggedObject)o, (boolean)false);
                        break block0;
                    }
                    case 1: {
                        this.crls = ASN1Set.getInstance((ASN1TaggedObject)o, (boolean)false);
                        break block0;
                    }
                }
                throw new IllegalArgumentException("Bad tag in OriginatorInfo: " + o.getTagNo());
            }
            case 2: {
                this.certs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(0)), (boolean)false);
                this.crls = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(1)), (boolean)false);
                break;
            }
            default: {
                throw new IllegalArgumentException("OriginatorInfo too big");
            }
        }
    }

    public static OriginatorInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return OriginatorInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static OriginatorInfo getInstance(Object obj) {
        if (obj instanceof OriginatorInfo) {
            return (OriginatorInfo)((Object)obj);
        }
        if (obj != null) {
            return new OriginatorInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Set getCertificates() {
        return this.certs;
    }

    public ASN1Set getCRLs() {
        return this.crls;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.certs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.certs));
        }
        if (this.crls != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.crls));
        }
        return new DERSequence(v);
    }
}

