/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;

public class BasePublicEncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int eciesNistP256 = 0;
    public static final int eciesBrainpoolP256r1 = 1;
    private final int choice;
    private final ASN1Encodable basePublicEncryptionKey;

    private BasePublicEncryptionKey(ASN1TaggedObject dto) {
        this.choice = dto.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: {
                this.basePublicEncryptionKey = EccP256CurvePoint.getInstance(dto.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + dto.getTagNo());
            }
        }
    }

    public BasePublicEncryptionKey(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.basePublicEncryptionKey = value;
    }

    public static BasePublicEncryptionKey getInstance(Object objectAt) {
        if (objectAt instanceof BasePublicEncryptionKey) {
            return (BasePublicEncryptionKey)((Object)objectAt);
        }
        if (objectAt != null) {
            return new BasePublicEncryptionKey(ASN1TaggedObject.getInstance((Object)objectAt, (int)128));
        }
        return null;
    }

    public static BasePublicEncryptionKey eciesNistP256(EccP256CurvePoint point) {
        return new BasePublicEncryptionKey(0, (ASN1Encodable)point);
    }

    public static BasePublicEncryptionKey eciesBrainpoolP256r1(EccP256CurvePoint point) {
        return new BasePublicEncryptionKey(1, (ASN1Encodable)point);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getBasePublicEncryptionKey() {
        return this.basePublicEncryptionKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.basePublicEncryptionKey);
    }

    public static class Builder {
        private int choice;
        private ASN1Encodable value;

        public Builder setChoice(int choice) {
            this.choice = choice;
            return this;
        }

        public Builder setValue(EccCurvePoint value) {
            this.value = value;
            return this;
        }

        public BasePublicEncryptionKey createBasePublicEncryptionKey() {
            return new BasePublicEncryptionKey(this.choice, this.value);
        }
    }
}

