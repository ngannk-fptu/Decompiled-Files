/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.GF2Polynomial;
import org.bouncycastle.math.field.GenericPolynomialExtensionField;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.field.PrimeField;
import org.bouncycastle.util.BigIntegers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class FiniteFields {
    static final FiniteField GF_2 = new PrimeField(BigInteger.valueOf(2L));
    static final FiniteField GF_3 = new PrimeField(BigInteger.valueOf(3L));

    public static PolynomialExtensionField getBinaryExtensionField(int[] exponents) {
        if (exponents[0] != 0) {
            throw new IllegalArgumentException("Irreducible polynomials in GF(2) must have constant term");
        }
        for (int i = 1; i < exponents.length; ++i) {
            if (exponents[i] > exponents[i - 1]) continue;
            throw new IllegalArgumentException("Polynomial exponents must be monotonically increasing");
        }
        return new GenericPolynomialExtensionField(GF_2, new GF2Polynomial(exponents));
    }

    public static FiniteField getPrimeField(BigInteger characteristic) {
        int bitLength = characteristic.bitLength();
        if (characteristic.signum() <= 0 || bitLength < 2) {
            throw new IllegalArgumentException("'characteristic' must be >= 2");
        }
        if (bitLength < 3) {
            switch (BigIntegers.intValueExact(characteristic)) {
                case 2: {
                    return GF_2;
                }
                case 3: {
                    return GF_3;
                }
            }
        }
        return new PrimeField(characteristic);
    }
}

