/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.constraints.ModCheck;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

@Deprecated
public class ModCheckValidator
extends ModCheckBase
implements ConstraintValidator<ModCheck, CharSequence> {
    private int multiplier;
    private ModCheck.ModType modType;

    public void initialize(ModCheck constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitPosition(), constraintAnnotation.ignoreNonDigitCharacters());
        this.modType = constraintAnnotation.modType();
        this.multiplier = constraintAnnotation.multiplier();
    }

    @Override
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult = -1;
        int checkValue = this.extractDigit(checkDigit);
        if (this.modType.equals((Object)ModCheck.ModType.MOD11)) {
            modResult = ModUtil.calculateMod11Check(digits, this.multiplier);
            if (modResult == 10 || modResult == 11) {
                modResult = 0;
            }
        } else {
            modResult = ModUtil.calculateLuhnMod10Check(digits);
        }
        return checkValue == modResult;
    }
}

