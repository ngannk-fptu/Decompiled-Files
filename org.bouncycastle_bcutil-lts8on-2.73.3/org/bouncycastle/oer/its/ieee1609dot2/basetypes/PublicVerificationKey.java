/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;

public class PublicVerificationKey
extends ASN1Object
implements ASN1Choice {
    public static final int ecdsaNistP256 = 0;
    public static final int ecdsaBrainpoolP256r1 = 1;
    public static final int ecdsaBrainpoolP384r1 = 2;
    private final int choice;
    private final ASN1Encodable publicVerificationKey;

    public PublicVerificationKey(int choice, ASN1Encodable curvePoint) {
        this.choice = choice;
        this.publicVerificationKey = curvePoint;
    }

    private PublicVerificationKey(ASN1TaggedObject taggedObject) {
        this.choice = taggedObject.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: {
                this.publicVerificationKey = EccP256CurvePoint.getInstance(taggedObject.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.publicVerificationKey = EccP384CurvePoint.getInstance(taggedObject.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + taggedObject.getTagNo());
    }

    public static PublicVerificationKey ecdsaNistP256(EccP256CurvePoint point) {
        return new PublicVerificationKey(0, (ASN1Encodable)point);
    }

    public static PublicVerificationKey ecdsaBrainpoolP256r1(EccP256CurvePoint point) {
        return new PublicVerificationKey(1, (ASN1Encodable)point);
    }

    public static PublicVerificationKey ecdsaBrainpoolP384r1(EccP384CurvePoint point) {
        return new PublicVerificationKey(2, (ASN1Encodable)point);
    }

    public static PublicVerificationKey getInstance(Object object) {
        if (object instanceof PublicVerificationKey) {
            return (PublicVerificationKey)((Object)object);
        }
        if (object != null) {
            return new PublicVerificationKey(ASN1TaggedObject.getInstance((Object)object, (int)128));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getPublicVerificationKey() {
        return this.publicVerificationKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.publicVerificationKey);
    }

    public static class Builder {
        private int choice;
        private ASN1Encodable curvePoint;

        public Builder setChoice(int choice) {
            this.choice = choice;
            return this;
        }

        public Builder setCurvePoint(EccCurvePoint curvePoint) {
            this.curvePoint = curvePoint;
            return this;
        }

        public Builder ecdsaNistP256(EccP256CurvePoint point) {
            this.curvePoint = point;
            return this;
        }

        public Builder ecdsaBrainpoolP256r1(EccP256CurvePoint point) {
            this.curvePoint = point;
            return this;
        }

        public Builder ecdsaBrainpoolP384r1(EccP384CurvePoint point) {
            this.curvePoint = point;
            return this;
        }

        public Builder extension(byte[] value) {
            this.curvePoint = new DEROctetString(value);
            return this;
        }

        public PublicVerificationKey createPublicVerificationKey() {
            return new PublicVerificationKey(this.choice, this.curvePoint);
        }
    }
}

