/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OneEightyDegreeInt
extends ASN1Object {
    private static final BigInteger loweBound = new BigInteger("-1799999999");
    private static final BigInteger upperBound = new BigInteger("1800000000");
    private static final BigInteger unknown = new BigInteger("1800000001");
    private final BigInteger value;

    public OneEightyDegreeInt(long degree) {
        this(BigInteger.valueOf(degree));
    }

    public OneEightyDegreeInt(BigInteger degree) {
        if (!degree.equals(unknown)) {
            if (degree.compareTo(loweBound) < 0) {
                throw new IllegalStateException("one eighty degree int cannot be less than -1799999999");
            }
            if (degree.compareTo(upperBound) > 0) {
                throw new IllegalStateException("one eighty degree int cannot be greater than 1800000000");
            }
        }
        this.value = degree;
    }

    private OneEightyDegreeInt(ASN1Integer i) {
        this(i.getValue());
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }

    public BigInteger getValue() {
        return this.value;
    }

    public static OneEightyDegreeInt getInstance(Object o) {
        if (o instanceof OneEightyDegreeInt) {
            return (OneEightyDegreeInt)((Object)o);
        }
        if (o != null) {
            return new OneEightyDegreeInt(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }

    private static BigInteger assertValue(BigInteger bi) {
        return bi;
    }
}

