/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.util.List;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.internal.constraintvalidators.hv.pl.PolishNumberValidator;

public class NIPValidator
extends PolishNumberValidator<NIP> {
    private static final int[] WEIGHTS_NIP = new int[]{6, 5, 7, 2, 3, 4, 5, 6, 7};

    public void initialize(NIP constraintAnnotation) {
        super.initialize(0, Integer.MAX_VALUE, -1, true);
    }

    @Override
    protected int[] getWeights(List<Integer> digits) {
        return WEIGHTS_NIP;
    }
}

