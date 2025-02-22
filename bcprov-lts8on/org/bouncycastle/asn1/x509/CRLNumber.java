/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

public class CRLNumber
extends ASN1Object {
    private BigInteger number;

    public CRLNumber(BigInteger number) {
        if (BigIntegers.ZERO.compareTo(number) > 0) {
            throw new IllegalArgumentException("Invalid CRL number : not in (0..MAX)");
        }
        this.number = number;
    }

    public BigInteger getCRLNumber() {
        return this.number;
    }

    public String toString() {
        return "CRLNumber: " + this.getCRLNumber();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.number);
    }

    public static CRLNumber getInstance(Object o) {
        if (o instanceof CRLNumber) {
            return (CRLNumber)o;
        }
        if (o != null) {
            return new CRLNumber(ASN1Integer.getInstance(o).getValue());
        }
        return null;
    }
}

