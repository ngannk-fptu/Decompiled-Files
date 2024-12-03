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

public class SymmAlgorithm
extends ASN1Enumerated {
    public static final SymmAlgorithm aes128Ccm = new SymmAlgorithm(BigInteger.ZERO);

    public SymmAlgorithm(BigInteger ordinal) {
        super(ordinal);
        this.assertValues();
    }

    private SymmAlgorithm(ASN1Enumerated enumerated) {
        super(enumerated.getValue());
        this.assertValues();
    }

    protected void assertValues() {
        switch (BigIntegers.intValueExact((BigInteger)this.getValue())) {
            case 0: {
                return;
            }
        }
        throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
    }

    public static SymmAlgorithm getInstance(Object src) {
        if (src instanceof SymmAlgorithm) {
            return (SymmAlgorithm)((Object)src);
        }
        if (src != null) {
            return new SymmAlgorithm(ASN1Enumerated.getInstance((Object)src));
        }
        return null;
    }
}

