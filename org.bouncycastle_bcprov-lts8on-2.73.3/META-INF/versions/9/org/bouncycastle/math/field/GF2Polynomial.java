/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class GF2Polynomial
implements Polynomial {
    protected final int[] exponents;

    GF2Polynomial(int[] exponents) {
        this.exponents = Arrays.clone(exponents);
    }

    @Override
    public int getDegree() {
        return this.exponents[this.exponents.length - 1];
    }

    @Override
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GF2Polynomial)) {
            return false;
        }
        GF2Polynomial other = (GF2Polynomial)obj;
        return Arrays.areEqual(this.exponents, other.exponents);
    }

    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}

