/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.eac.UnsignedInteger;

public class RSAPublicKey
extends PublicKeyDataObject {
    private ASN1ObjectIdentifier usage;
    private BigInteger modulus;
    private BigInteger exponent;
    private int valid = 0;
    private static int modulusValid = 1;
    private static int exponentValid = 2;

    RSAPublicKey(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.usage = ASN1ObjectIdentifier.getInstance(en.nextElement());
        block4: while (en.hasMoreElements()) {
            UnsignedInteger val = UnsignedInteger.getInstance(en.nextElement());
            switch (val.getTagNo()) {
                case 1: {
                    this.setModulus(val);
                    continue block4;
                }
                case 2: {
                    this.setExponent(val);
                    continue block4;
                }
            }
            throw new IllegalArgumentException("Unknown DERTaggedObject :" + val.getTagNo() + "-> not an Iso7816RSAPublicKeyStructure");
        }
        if (this.valid != 3) {
            throw new IllegalArgumentException("missing argument -> not an Iso7816RSAPublicKeyStructure");
        }
    }

    public RSAPublicKey(ASN1ObjectIdentifier usage, BigInteger modulus, BigInteger exponent) {
        this.usage = usage;
        this.modulus = modulus;
        this.exponent = exponent;
    }

    @Override
    public ASN1ObjectIdentifier getUsage() {
        return this.usage;
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getPublicExponent() {
        return this.exponent;
    }

    private void setModulus(UnsignedInteger modulus) {
        if ((this.valid & modulusValid) == 0) {
            this.valid |= modulusValid;
        } else {
            throw new IllegalArgumentException("Modulus already set");
        }
        this.modulus = modulus.getValue();
    }

    private void setExponent(UnsignedInteger exponent) {
        if ((this.valid & exponentValid) == 0) {
            this.valid |= exponentValid;
        } else {
            throw new IllegalArgumentException("Exponent already set");
        }
        this.exponent = exponent.getValue();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.usage);
        v.add((ASN1Encodable)new UnsignedInteger(1, this.getModulus()));
        v.add((ASN1Encodable)new UnsignedInteger(2, this.getPublicExponent()));
        return new DERSequence(v);
    }
}

