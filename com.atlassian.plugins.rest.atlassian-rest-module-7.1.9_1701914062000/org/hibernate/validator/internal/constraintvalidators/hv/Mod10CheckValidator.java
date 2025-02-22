/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class Mod10CheckValidator
extends ModCheckBase
implements ConstraintValidator<Mod10Check, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int multiplier;
    private int weight;

    public void initialize(Mod10Check constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitIndex(), constraintAnnotation.ignoreNonDigitCharacters());
        this.multiplier = constraintAnnotation.multiplier();
        this.weight = constraintAnnotation.weight();
        if (this.multiplier < 0) {
            throw LOG.getMultiplierCannotBeNegativeException(this.multiplier);
        }
        if (this.weight < 0) {
            throw LOG.getWeightCannotBeNegativeException(this.weight);
        }
    }

    @Override
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult = ModUtil.calculateMod10Check(digits, this.multiplier, this.weight);
        if (!Character.isDigit(checkDigit)) {
            return false;
        }
        int checkValue = this.extractDigit(checkDigit);
        return checkValue == modResult;
    }
}

