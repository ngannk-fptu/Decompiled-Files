/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class GeneralSubtree
extends ASN1Object {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private GeneralName base;
    private ASN1Integer minimum;
    private ASN1Integer maximum;

    private GeneralSubtree(ASN1Sequence seq) {
        this.base = GeneralName.getInstance(seq.getObjectAt(0));
        block0 : switch (seq.size()) {
            case 1: {
                break;
            }
            case 2: {
                ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
                switch (o.getTagNo()) {
                    case 0: {
                        this.minimum = ASN1Integer.getInstance(o, false);
                        break block0;
                    }
                    case 1: {
                        this.maximum = ASN1Integer.getInstance(o, false);
                        break block0;
                    }
                }
                throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
            }
            case 3: {
                ASN1TaggedObject oMin = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
                if (oMin.getTagNo() != 0) {
                    throw new IllegalArgumentException("Bad tag number for 'minimum': " + oMin.getTagNo());
                }
                this.minimum = ASN1Integer.getInstance(oMin, false);
                ASN1TaggedObject oMax = ASN1TaggedObject.getInstance(seq.getObjectAt(2));
                if (oMax.getTagNo() != 1) {
                    throw new IllegalArgumentException("Bad tag number for 'maximum': " + oMax.getTagNo());
                }
                this.maximum = ASN1Integer.getInstance(oMax, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
            }
        }
    }

    public GeneralSubtree(GeneralName base, BigInteger minimum, BigInteger maximum) {
        this.base = base;
        if (maximum != null) {
            this.maximum = new ASN1Integer(maximum);
        }
        this.minimum = minimum == null ? null : new ASN1Integer(minimum);
    }

    public GeneralSubtree(GeneralName base) {
        this(base, null, null);
    }

    public static GeneralSubtree getInstance(ASN1TaggedObject o, boolean explicit) {
        return new GeneralSubtree(ASN1Sequence.getInstance(o, explicit));
    }

    public static GeneralSubtree getInstance(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof GeneralSubtree) {
            return (GeneralSubtree)obj;
        }
        return new GeneralSubtree(ASN1Sequence.getInstance(obj));
    }

    public GeneralName getBase() {
        return this.base;
    }

    public BigInteger getMinimum() {
        if (this.minimum == null) {
            return ZERO;
        }
        return this.minimum.getValue();
    }

    public BigInteger getMaximum() {
        if (this.maximum == null) {
            return null;
        }
        return this.maximum.getValue();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.base);
        if (this.minimum != null && !this.minimum.hasValue(0)) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.minimum));
        }
        if (this.maximum != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.maximum));
        }
        return new DERSequence(v);
    }
}

