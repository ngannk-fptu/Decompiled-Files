/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class RSAPublicKey
extends ASN1Object {
    private BigInteger modulus;
    private BigInteger publicExponent;

    public static RSAPublicKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return RSAPublicKey.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RSAPublicKey getInstance(Object obj) {
        if (obj instanceof RSAPublicKey) {
            return (RSAPublicKey)obj;
        }
        if (obj != null) {
            return new RSAPublicKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public RSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    private RSAPublicKey(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.modulus = ASN1Integer.getInstance(e.nextElement()).getPositiveValue();
        this.publicExponent = ASN1Integer.getInstance(e.nextElement()).getPositiveValue();
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(new ASN1Integer(this.getModulus()));
        v.add(new ASN1Integer(this.getPublicExponent()));
        return new DERSequence(v);
    }
}

