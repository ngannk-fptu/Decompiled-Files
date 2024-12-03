/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class HashAlgorithm
extends ASN1Enumerated {
    public static final HashAlgorithm sha256 = new HashAlgorithm(BigInteger.ZERO);
    public static final HashAlgorithm sha384 = new HashAlgorithm(BigIntegers.ONE);

    public HashAlgorithm(BigInteger integer) {
        super(integer);
        this.assertValues();
    }

    private HashAlgorithm(ASN1Enumerated enumerated) {
        this(enumerated.getValue());
    }

    public static HashAlgorithm getInstance(Object src) {
        if (src instanceof HashAlgorithm) {
            return (HashAlgorithm)((Object)src);
        }
        if (src != null) {
            return new HashAlgorithm(ASN1Enumerated.getInstance((Object)src));
        }
        return null;
    }

    protected void assertValues() {
        switch (BigIntegers.intValueExact((BigInteger)this.getValue())) {
            case 0: 
            case 1: {
                return;
            }
        }
        throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
    }
}

