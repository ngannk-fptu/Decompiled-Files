/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.constraints.pl.PESEL;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

public class PESELValidator
extends ModCheckBase
implements ConstraintValidator<PESEL, CharSequence> {
    private static final int[] WEIGHTS_PESEL = new int[]{1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

    public void initialize(PESEL constraintAnnotation) {
        super.initialize(0, Integer.MAX_VALUE, -1, false);
    }

    @Override
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        Collections.reverse(digits);
        if (digits.size() != WEIGHTS_PESEL.length) {
            return false;
        }
        int modResult = ModUtil.calculateModXCheckWithWeights(digits, 10, Integer.MAX_VALUE, WEIGHTS_PESEL);
        switch (modResult) {
            case 10: {
                return checkDigit == '0';
            }
        }
        return Character.isDigit(checkDigit) && modResult == this.extractDigit(checkDigit);
    }
}

