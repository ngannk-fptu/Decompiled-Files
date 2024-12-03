/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class PduFunctionalType
extends ASN1Object {
    private static final BigInteger MAX = BigInteger.valueOf(255L);
    public static final PduFunctionalType tlsHandshake = new PduFunctionalType(1L);
    public static final PduFunctionalType iso21177ExtendedAuth = new PduFunctionalType(2L);
    private final BigInteger functionalType;

    public PduFunctionalType(long value) {
        this(BigInteger.valueOf(value));
    }

    public PduFunctionalType(BigInteger value) {
        this.functionalType = PduFunctionalType.assertValue(value);
    }

    public PduFunctionalType(byte[] bytes) {
        this(new BigInteger(bytes));
    }

    private PduFunctionalType(ASN1Integer instance) {
        this(instance.getValue());
    }

    public static PduFunctionalType getInstance(Object src) {
        if (src instanceof PduFunctionalType) {
            return (PduFunctionalType)((Object)src);
        }
        if (src != null) {
            return new PduFunctionalType(ASN1Integer.getInstance((Object)src));
        }
        return null;
    }

    public BigInteger getFunctionalType() {
        return this.functionalType;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.functionalType);
    }

    private static BigInteger assertValue(BigInteger value) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException("value less than 0");
        }
        if (value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value exceeds " + MAX);
        }
        return value;
    }
}

