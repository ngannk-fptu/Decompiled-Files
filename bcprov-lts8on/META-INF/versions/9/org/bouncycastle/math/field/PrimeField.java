/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.math.field.FiniteField;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class PrimeField
implements FiniteField {
    protected final BigInteger characteristic;

    PrimeField(BigInteger characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public BigInteger getCharacteristic() {
        return this.characteristic;
    }

    @Override
    public int getDimension() {
        return 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PrimeField)) {
            return false;
        }
        PrimeField other = (PrimeField)obj;
        return this.characteristic.equals(other.characteristic);
    }

    public int hashCode() {
        return this.characteristic.hashCode();
    }
}

