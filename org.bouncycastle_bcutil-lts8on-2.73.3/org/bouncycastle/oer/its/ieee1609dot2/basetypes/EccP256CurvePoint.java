/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.math.ec.ECPoint
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.util.Arrays;

public class EccP256CurvePoint
extends EccCurvePoint
implements ASN1Choice {
    public static final int xonly = 0;
    public static final int fill = 1;
    public static final int compressedY0 = 2;
    public static final int compressedY1 = 3;
    public static final int uncompressedP256 = 4;
    private final int choice;
    private final ASN1Encodable eccp256CurvePoint;

    public EccP256CurvePoint(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.eccp256CurvePoint = value;
    }

    private EccP256CurvePoint(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (ato.getTagNo()) {
            case 1: {
                this.eccp256CurvePoint = ASN1Null.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            case 0: 
            case 2: 
            case 3: {
                this.eccp256CurvePoint = ASN1OctetString.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            case 4: {
                this.eccp256CurvePoint = Point256.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + ato.getTagNo());
            }
        }
    }

    public static EccP256CurvePoint xOnly(ASN1OctetString value) {
        return new EccP256CurvePoint(0, (ASN1Encodable)value);
    }

    public static EccP256CurvePoint xOnly(byte[] value) {
        return new EccP256CurvePoint(0, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])value)));
    }

    public static EccP256CurvePoint fill() {
        return new EccP256CurvePoint(1, (ASN1Encodable)DERNull.INSTANCE);
    }

    public static EccP256CurvePoint compressedY0(ASN1OctetString octetString) {
        return new EccP256CurvePoint(2, (ASN1Encodable)octetString);
    }

    public static EccP256CurvePoint compressedY1(ASN1OctetString octetString) {
        return new EccP256CurvePoint(3, (ASN1Encodable)octetString);
    }

    public static EccP256CurvePoint compressedY0(byte[] octetString) {
        return new EccP256CurvePoint(2, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])octetString)));
    }

    public static EccP256CurvePoint compressedY1(byte[] octetString) {
        return new EccP256CurvePoint(3, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])octetString)));
    }

    public static EccP256CurvePoint uncompressedP256(Point256 point256) {
        return new EccP256CurvePoint(4, (ASN1Encodable)point256);
    }

    public static EccP256CurvePoint uncompressedP256(BigInteger x, BigInteger y) {
        return new EccP256CurvePoint(4, (ASN1Encodable)Point256.builder().setX(x).setY(y).createPoint256());
    }

    public static EccP256CurvePoint createEncodedPoint(byte[] encoded) {
        if (encoded[0] == 2) {
            byte[] copy = new byte[encoded.length - 1];
            System.arraycopy(encoded, 1, copy, 0, copy.length);
            return new EccP256CurvePoint(2, (ASN1Encodable)new DEROctetString(copy));
        }
        if (encoded[0] == 3) {
            byte[] copy = new byte[encoded.length - 1];
            System.arraycopy(encoded, 1, copy, 0, copy.length);
            return new EccP256CurvePoint(3, (ASN1Encodable)new DEROctetString(copy));
        }
        if (encoded[0] == 4) {
            return new EccP256CurvePoint(4, (ASN1Encodable)new Point256((ASN1OctetString)new DEROctetString(Arrays.copyOfRange((byte[])encoded, (int)1, (int)34)), (ASN1OctetString)new DEROctetString(Arrays.copyOfRange((byte[])encoded, (int)34, (int)66))));
        }
        throw new IllegalArgumentException("unrecognised encoding " + encoded[0]);
    }

    public EccP256CurvePoint createCompressed(ECPoint point) {
        int choice = 0;
        byte[] encoded = point.getEncoded(true);
        if (encoded[0] == 2) {
            choice = 2;
        } else if (encoded[0] == 3) {
            choice = 3;
        }
        byte[] copy = new byte[encoded.length - 1];
        System.arraycopy(encoded, 0, copy, 0, copy.length);
        return new EccP256CurvePoint(choice, (ASN1Encodable)new DEROctetString(copy));
    }

    public static EccP256CurvePoint getInstance(Object object) {
        if (object instanceof EccP256CurvePoint) {
            return (EccP256CurvePoint)((Object)object);
        }
        if (object != null) {
            return new EccP256CurvePoint(ASN1TaggedObject.getInstance((Object)object, (int)128));
        }
        return null;
    }

    public ASN1Encodable getEccp256CurvePoint() {
        return this.eccp256CurvePoint;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.eccp256CurvePoint);
    }

    @Override
    public byte[] getEncodedPoint() {
        byte[] key;
        switch (this.choice) {
            case 2: {
                byte[] originalKey = DEROctetString.getInstance((Object)this.eccp256CurvePoint).getOctets();
                key = new byte[originalKey.length + 1];
                key[0] = 2;
                System.arraycopy(originalKey, 0, key, 1, originalKey.length);
                break;
            }
            case 3: {
                byte[] originalKey = DEROctetString.getInstance((Object)this.eccp256CurvePoint).getOctets();
                key = new byte[originalKey.length + 1];
                key[0] = 3;
                System.arraycopy(originalKey, 0, key, 1, originalKey.length);
                break;
            }
            case 4: {
                Point256 point256 = Point256.getInstance(this.eccp256CurvePoint);
                byte[] x = point256.getX().getOctets();
                byte[] y = point256.getY().getOctets();
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

