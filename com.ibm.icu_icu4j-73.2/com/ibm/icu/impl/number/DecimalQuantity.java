/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.number.Modifier;
import com.ibm.icu.text.PluralRules;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.FieldPosition;

public interface DecimalQuantity
extends PluralRules.IFixedDecimal {
    public void setMinInteger(int var1);

    public void setMinFraction(int var1);

    public void applyMaxInteger(int var1);

    public void roundToIncrement(BigDecimal var1, MathContext var2);

    public void roundToNickel(int var1, MathContext var2);

    public void roundToMagnitude(int var1, MathContext var2);

    public void roundToInfinity();

    public void multiplyBy(BigDecimal var1);

    public void negate();

    public void adjustMagnitude(int var1);

    public int getMagnitude() throws ArithmeticException;

    public int getExponent();

    public void adjustExponent(int var1);

    public void resetExponent();

    public boolean isZeroish();

    public boolean isNegative();

    public Modifier.Signum signum();

    @Override
    public boolean isInfinite();

    @Override
    public boolean isNaN();

    public double toDouble();

    public BigDecimal toBigDecimal();

    public long toLong(boolean var1);

    public void setToBigDecimal(BigDecimal var1);

    public int maxRepresentableDigits();

    public StandardPlural getStandardPlural(PluralRules var1);

    public byte getDigit(int var1);

    public int getUpperDisplayMagnitude();

    public int getLowerDisplayMagnitude();

    public String toPlainString();

    public String toExponentString();

    public DecimalQuantity createCopy();

    public void copyFrom(DecimalQuantity var1);

    public long getPositionFingerprint();

    public void populateUFieldPosition(FieldPosition var1);
}

