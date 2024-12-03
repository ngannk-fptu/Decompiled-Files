/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class GenericPolynomialExtensionField
implements PolynomialExtensionField {
    protected final FiniteField subfield;
    protected final Polynomial minimalPolynomial;

    GenericPolynomialExtensionField(FiniteField subfield, Polynomial polynomial) {
        this.subfield = subfield;
        this.minimalPolynomial = polynomial;
    }

    @Override
    public BigInteger getCharacteristic() {
        return this.subfield.getCharacteristic();
    }

    @Override
    public int getDimension() {
        return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
    }

    @Override
    public FiniteField getSubfield() {
        return this.subfield;
    }

    @Override
    public int getDegree() {
        return this.minimalPolynomial.getDegree();
    }

    @Override
    public Polynomial getMinimalPolynomial() {
        return this.minimalPolynomial;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenericPolynomialExtensionField)) {
            return false;
        }
        GenericPolynomialExtensionField other = (GenericPolynomialExtensionField)obj;
        return this.subfield.equals(other.subfield) && this.minimalPolynomial.equals(other.minimalPolynomial);
    }

    public int hashCode() {
        return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
    }
}

