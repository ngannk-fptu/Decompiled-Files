/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class EAN13CheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = 1726347093230424107L;
    public static final CheckDigit EAN13_CHECK_DIGIT = new EAN13CheckDigit();
    private static final int[] POSITION_WEIGHT = new int[]{3, 1};

    public EAN13CheckDigit() {
        super(10);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        return charValue * weight;
    }
}

