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

public class NinetyDegreeInt
extends ASN1Object {
    private static final BigInteger loweBound = new BigInteger("-900000000");
    private static final BigInteger upperBound = new BigInteger("900000000");
    private static final BigInteger unknown = new BigInteger("900000001");
    private final BigInteger value;

    public NinetyDegreeInt(long degree) {
        this(BigInteger.valueOf(degree));
    }

    public NinetyDegreeInt(BigInteger degree) {
        if (!degree.equals(unknown)) {
            if (degree.compareTo(loweBound) < 0) {
                throw new IllegalStateException("ninety degree int cannot be less than -900000000");
            }
            if (degree.compareTo(upperBound) > 0) {
                throw new IllegalStateException("ninety degree int cannot be greater than 900000000");
            }
        }
        this.value = degree;
    }

    private NinetyDegreeInt(ASN1Integer i) {
        this(i.getValue());
    }

    public BigInteger getValue() {
        return this.value;
    }

    public static NinetyDegreeInt getInstance(Object o) {
        if (o instanceof NinetyDegreeInt) {
            return (NinetyDegreeInt)((Object)o);
        }
        if (o != null) {
            return new NinetyDegreeInt(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }
}

