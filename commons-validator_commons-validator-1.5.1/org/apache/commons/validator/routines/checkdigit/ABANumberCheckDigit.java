/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class ABANumberCheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = -8255937433810380145L;
    public static final CheckDigit ABAN_CHECK_DIGIT = new ABANumberCheckDigit();
    private static final int[] POSITION_WEIGHT = new int[]{3, 1, 7};

    public ABANumberCheckDigit() {
        super(10);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 3];
        return charValue * weight;
    }
}

