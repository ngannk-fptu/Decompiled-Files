/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.util.Arrays;

public class EccP384CurvePoint
extends EccCurvePoint
implements ASN1Choice {
    public static final int xonly = 0;
    public static final int fill = 1;
    public static final int compressedY0 = 2;
    public static final int compressedY1 = 3;
    public static final int uncompressedP384 = 4;
    private final int choice;
    private final ASN1Encodable eccP384CurvePoint;

    public EccP384CurvePoint(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.eccP384CurvePoint = value;
    }

    private EccP384CurvePoint(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (ato.getTagNo()) {
            case 1: {
                this.eccP384CurvePoint = ASN1Null.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            case 0: 
            case 2: 
            case 3: {
                this.eccP384CurvePoint = ASN1OctetString.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            case 4: {
                this.eccP384CurvePoint = ASN1Sequence.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + ato.getTagNo());
            }
        }
    }

    public static EccP384CurvePoint getInstance(Object object) {
        if (object instanceof EccP384CurvePoint) {
            return (EccP384CurvePoint)((Object)object);
        }
        if (object != null) {
            return new EccP384CurvePoint(ASN1TaggedObject.getInstance((Object)object, (int)128));
        }
        return null;
    }

    public static EccP384CurvePoint xOnly(ASN1OctetString value) {
        return new EccP384CurvePoint(0, (ASN1Encodable)value);
    }

    public static EccP384CurvePoint xOnly(byte[] value) {
        return new EccP384CurvePoint(0, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])value)));
    }

    public static EccP384CurvePoint fill() {
        return new EccP384CurvePoint(1, (ASN1Encodable)DERNull.INSTANCE);
    }

    public static EccP384CurvePoint compressedY0(ASN1OctetString octetString) {
        return new EccP384CurvePoint(2, (ASN1Encodable)octetString);
    }

    public static EccP384CurvePoint compressedY1(ASN1OctetString octetString) {
        return new EccP384CurvePoint(3, (ASN1Encodable)octetString);
    }

    public static EccP384CurvePoint compressedY0(byte[] octetString) {
        return new EccP384CurvePoint(2, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])octetString)));
    }

    public static EccP384CurvePoint compressedY1(byte[] octetString) {
        return new EccP384CurvePoint(3, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])octetString)));
    }

    public static EccP384CurvePoint uncompressedP384(Point384 point384) {
        return new EccP384CurvePoint(4, (ASN1Encodable)point384);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEccP384CurvePoint() {
        return this.eccP384CurvePoint;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.eccP384CurvePoint);
    }

    @Override
    public byte[] getEncodedPoint() {
        byte[] key;
        switch (this.choice) {
            case 2: {
                byte[] originalKey = DEROctetString.getInstance((Object)this.eccP384CurvePoint).getOctets();
                key = new byte[originalKey.length + 1];
                key[0] = 2;
                System.arraycopy(originalKey, 0, key, 1, originalKey.length);
                break;
            }
            case 3: {
                byte[] originalKey = DEROctetString.getInstance((Object)this.eccP384CurvePoint).getOctets();
                key = new byte[originalKey.length + 1];
                key[0] = 3;
                System.arraycopy(originalKey, 0, key, 1, originalKey.length);
                break;
            }
            case 4: {
                ASN1Sequence sequence = ASN1Sequence.getInstance((Object)this.eccP384CurvePoint);
                byte[] x = DEROctetString.getInstance((Object)sequence.getObjectAt(0)).getOctets();
                byte[] y = DEROctetString.getInstance((Object)sequence.getObjectAt(1)).getOctets();
                key = Arrays.concatenate((byte[])new byte[]{4}, (byte[])x, (byte[])y);
                break;
            }
            case 0: {
                throw new IllegalStateException("x Only not implemented");
            }
            default: {
                throw new IllegalStateException("unknown point choice");
            }
        }
        return key;
    }
}

