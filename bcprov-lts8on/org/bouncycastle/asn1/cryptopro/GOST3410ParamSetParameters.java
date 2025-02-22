/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cryptopro;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class GOST3410ParamSetParameters
extends ASN1Object {
    int keySize;
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer a;

    public static GOST3410ParamSetParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return GOST3410ParamSetParameters.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static GOST3410ParamSetParameters getInstance(Object obj) {
        if (obj == null || obj instanceof GOST3410ParamSetParameters) {
            return (GOST3410ParamSetParameters)obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new GOST3410ParamSetParameters((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + obj.getClass().getName());
    }

    public GOST3410ParamSetParameters(int keySize, BigInteger p, BigInteger q, BigInteger a) {
        this.keySize = keySize;
        this.p = new ASN1Integer(p);
        this.q = new ASN1Integer(q);
        this.a = new ASN1Integer(a);
    }

    public GOST3410ParamSetParameters(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.keySize = ((ASN1Integer)e.nextElement()).intValueExact();
        this.p = (ASN1Integer)e.nextElement();
        this.q = (ASN1Integer)e.nextElement();
        this.a = (ASN1Integer)e.nextElement();
    }

    public int getKeySize() {
        return this.keySize;
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }

    public BigInteger getA() {
        return this.a.getPositiveValue();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(new ASN1Integer(this.keySize));
        v.add(this.p);
        v.add(this.q);
        v.add(this.a);
        return new DERSequence(v);
    }
}

