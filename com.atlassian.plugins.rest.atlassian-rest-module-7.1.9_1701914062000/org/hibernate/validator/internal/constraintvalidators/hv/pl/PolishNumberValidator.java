/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

public abstract class PolishNumberValidator<T extends Annotation>
extends ModCheckBase
implements ConstraintValidator<T, CharSequence> {
    @Override
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        Collections.reverse(digits);
        int[] weights = this.getWeights(digits);
        if (weights.length != digits.size()) {
            return false;
        }
        int modResult = 11 - ModUtil.calculateModXCheckWithWeights(digits, 11, Integer.MAX_VALUE, weights);
        switch (modResult) {
            case 10: 
            case 11: {
                return checkDigit == '0';
            }
        }
        return Character.isDigit(checkDigit) && modResult == this.extractDigit(checkDigit);
    }

    protected abstract int[] getWeights(List<Integer> var1);
}

