/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ElGamalParameter
extends ASN1Object {
    ASN1Integer p;
    ASN1Integer g;

    public ElGamalParameter(BigInteger p, BigInteger g) {
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
    }

    private ElGamalParameter(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.p = (ASN1Integer)e.nextElement();
        this.g = (ASN1Integer)e.nextElement();
    }

    public static ElGamalParameter getInstance(Object o) {
        if (o instanceof ElGamalParameter) {
            return (ElGamalParameter)o;
        }
        if (o != null) {
            return new ElGamalParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.g.getPositiveValue();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.p);
        v.add(this.g);
        return new DERSequence(v);
    }
}

