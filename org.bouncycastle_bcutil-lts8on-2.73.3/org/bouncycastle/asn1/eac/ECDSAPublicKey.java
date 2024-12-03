/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.eac.UnsignedInteger;
import org.bouncycastle.util.Arrays;

public class ECDSAPublicKey
extends PublicKeyDataObject {
    private ASN1ObjectIdentifier usage;
    private BigInteger primeModulusP;
    private BigInteger firstCoefA;
    private BigInteger secondCoefB;
    private byte[] basePointG;
    private BigInteger orderOfBasePointR;
    private byte[] publicPointY;
    private BigInteger cofactorF;
    private int options;
    private static final int P = 1;
    private static final int A = 2;
    private static final int B = 4;
    private static final int G = 8;
    private static final int R = 16;
    private static final int Y = 32;
    private static final int F = 64;

    ECDSAPublicKey(ASN1Sequence seq) throws IllegalArgumentException {
        Enumeration en = seq.getObjects();
        this.usage = ASN1ObjectIdentifier.getInstance(en.nextElement());
        this.options = 0;
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            if (obj instanceof ASN1TaggedObject) {
                ASN1TaggedObject to = (ASN1TaggedObject)obj;
                switch (to.getTagNo()) {
                    case 1: {
                        this.setPrimeModulusP(UnsignedInteger.getInstance(to).getValue());
                        break;
                    }
                    case 2: {
                        this.setFirstCoefA(UnsignedInteger.getInstance(to).getValue());
                        break;
                    }
                    case 3: {
                        this.setSecondCoefB(UnsignedInteger.getInstance(to).getValue());
                        break;
                    }
                    case 4: {
                        this.setBasePointG(ASN1OctetString.getInstance((ASN1TaggedObject)to, (boolean)false));
                        break;
                    }
                    case 5: {
                        this.setOrderOfBasePointR(UnsignedInteger.getInstance(to).getValue());
                        break;
                    }
                    case 6: {
                        this.setPublicPointY(ASN1OctetString.getInstance((ASN1TaggedObject)to, (boolean)false));
                        break;
                    }
                    case 7: {
                        this.setCofactorF(UnsignedInteger.getInstance(to).getValue());
                        break;
                    }
                    default: {
                        this.options = 0;
                        throw new IllegalArgumentException("Unknown Object Identifier!");
                    }
                }
                continue;
            }
            throw new IllegalArgumentException("Unknown Object Identifier!");
        }
        if (this.options != 32 && this.options != 127) {
            throw new IllegalArgumentException("All options must be either present or absent!");
        }
    }

    public ECDSAPublicKey(ASN1ObjectIdentifier usage, byte[] ppY) throws IllegalArgumentException {
        this.usage = usage;
        this.setPublicPointY((ASN1OctetString)new DEROctetString(ppY));
    }

    public ECDSAPublicKey(ASN1ObjectIdentifier usage, BigInteger p, BigInteger a, BigInteger b, byte[] basePoint, BigInteger order, byte[] publicPoint, int cofactor) {
        this.usage = usage;
        this.setPrimeModulusP(p);
        this.setFirstCoefA(a);
        this.setSecondCoefB(b);
        this.setBasePointG((ASN1OctetString)new DEROctetString(basePoint));
        this.setOrderOfBasePointR(order);
        this.setPublicPointY((ASN1OctetString)new DEROctetString(publicPoint));
        this.setCofactorF(BigInteger.valueOf(cofactor));
    }

    @Override
    public ASN1ObjectIdentifier getUsage() {
        return this.usage;
    }

    public byte[] getBasePointG() {
        if ((this.options & 8) != 0) {
            return Arrays.clone((byte[])this.basePointG);
        }
        return null;
    }

    private void setBasePointG(ASN1OctetString basePointG) throws IllegalArgumentException {
        if ((this.options & 8) == 0) {
            this.options |= 8;
        } else {
            throw new IllegalArgumentException("Base Point G already set");
        }
        this.basePointG = basePointG.getOctets();
    }

    public BigInteger getCofactorF() {
        if ((this.options & 0x40) != 0) {
            return this.cofactorF;
        }
        return null;
    }

    private void setCofactorF(BigInteger cofactorF) throws IllegalArgumentException {
        if ((this.options & 0x40) == 0) {
            this.options |= 0x40;
        } else {
            throw new IllegalArgumentException("Cofactor F already set");
        }
        this.cofactorF = cofactorF;
    }

    public BigInteger getFirstCoefA() {
        if ((this.options & 2) != 0) {
            return this.firstCoefA;
        }
        return null;
    }

    private void setFirstCoefA(BigInteger firstCoefA) throws IllegalArgumentException {
        if ((this.options & 2) == 0) {
            this.options |= 2;
        } else {
            throw new IllegalArgumentException("First Coef A already set");
        }
        this.firstCoefA = firstCoefA;
    }

    public BigInteger getOrderOfBasePointR() {
        if ((this.options & 0x10) != 0) {
            return this.orderOfBasePointR;
        }
        return null;
    }

    private void setOrderOfBasePointR(BigInteger orderOfBasePointR) throws IllegalArgumentException {
        if ((this.options & 0x10) == 0) {
            this.options |= 0x10;
        } else {
            throw new IllegalArgumentException("Order of base point R already set");
        }
        this.orderOfBasePointR = orderOfBasePointR;
    }

    public BigInteger getPrimeModulusP() {
        if ((this.options & 1) != 0) {
            return this.primeModulusP;
        }
        return null;
    }

    private void setPrimeModulusP(BigInteger primeModulusP) {
        if ((this.options & 1) == 0) {
            this.options |= 1;
        } else {
            throw new IllegalArgumentException("Prime Modulus P already set");
        }
        this.primeModulusP = primeModulusP;
    }

    public byte[] getPublicPointY() {
        if ((this.options & 0x20) != 0) {
            return Arrays.clone((byte[])this.publicPointY);
        }
        return null;
    }

    private void setPublicPointY(ASN1OctetString publicPointY) throws IllegalArgumentException {
        if ((this.options & 0x20) == 0) {
            this.options |= 0x20;
        } else {
            throw new IllegalArgumentException("Public Point Y already set");
        }
        this.publicPointY = publicPointY.getOctets();
    }

    public BigInteger getSecondCoefB() {
        if ((this.options & 4) != 0) {
            return this.secondCoefB;
        }
        return null;
    }

    private void setSecondCoefB(BigInteger secondCoefB) throws IllegalArgumentException {
        if ((this.options & 4) == 0) {
            this.options |= 4;
        } else {
            throw new IllegalArgumentException("Second Coef B already set");
        }
        this.secondCoefB = secondCoefB;
    }

    public boolean hasParameters() {
        return this.primeModulusP != null;
    }

    public ASN1EncodableVector getASN1EncodableVector(ASN1ObjectIdentifier oid, boolean publicPointOnly) {
        ASN1EncodableVector v = new ASN1EncodableVector(8);
        v.add((ASN1Encodable)oid);
        if (!publicPointOnly) {
            v.add((ASN1Encodable)new UnsignedInteger(1, this.getPrimeModulusP()));
            v.add((ASN1Encodable)new UnsignedInteger(2, this.getFirstCoefA()));
            v.add((ASN1Encodable)new UnsignedInteger(3, this.getSecondCoefB()));
            v.add((ASN1Encodable)new DERTaggedObject(false, 4, (ASN1Encodable)new DEROctetString(this.getBasePointG())));
            v.add((ASN1Encodable)new UnsignedInteger(5, this.getOrderOfBasePointR()));
        }
        v.add((ASN1Encodable)new DERTaggedObject(false, 6, (ASN1Encodable)new DEROctetString(this.getPublicPointY())));
        if (!publicPointOnly) {
            v.add((ASN1Encodable)new UnsignedInteger(7, this.getCofactorF()));
        }
        return v;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.getASN1EncodableVector(this.usage, !this.hasParameters()));
    }
}

