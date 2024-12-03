/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Digits
 */
package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Digits;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class DigitsValidatorForNumber
implements ConstraintValidator<Digits, Number> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int maxIntegerLength;
    private int maxFractionLength;

    public void initialize(Digits constraintAnnotation) {
        this.maxIntegerLength = constraintAnnotation.integer();
        this.maxFractionLength = constraintAnnotation.fraction();
        this.validateParameters();
    }

    public boolean isValid(Number num, ConstraintValidatorContext constraintValidatorContext) {
        if (num == null) {
            return true;
        }
        BigDecimal bigNum = num instanceof BigDecimal ? (BigDecimal)num : new BigDecimal(num.toString()).stripTrailingZeros();
        int integerPartLength = bigNum.precision() - bigNum.scale();
        int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();
        return this.maxIntegerLength >= integerPartLength && this.maxFractionLength >= fractionPartLength;
    }

    private void validateParameters() {
        if (this.maxIntegerLength < 0) {
            throw LOG.getInvalidLengthForIntegerPartException();
        }
        if (this.maxFractionLength < 0) {
            throw LOG.getInvalidLengthForFractionPartException();
        }
    }
}

