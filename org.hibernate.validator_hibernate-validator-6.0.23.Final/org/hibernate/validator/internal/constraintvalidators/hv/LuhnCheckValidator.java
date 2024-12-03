/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

public class LuhnCheckValidator
extends ModCheckBase
implements ConstraintValidator<LuhnCheck, CharSequence> {
    public void initialize(LuhnCheck constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitIndex(), constraintAnnotation.ignoreNonDigitCharacters());
    }

    @Override
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult = ModUtil.calculateLuhnMod10Check(digits);
        if (!Character.isDigit(checkDigit)) {
            return false;
        }
        int checkValue = this.extractDigit(checkDigit);
        return checkValue == modResult;
    }
}

