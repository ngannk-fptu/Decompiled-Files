/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.annotations;

import com.opensymphony.xwork2.validator.annotations.ValidatorType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface DoubleRangeFieldValidator {
    public String minInclusive() default "";

    public String minInclusiveExpression() default "";

    public String maxInclusive() default "";

    public String maxInclusiveExpression() default "";

    public String minExclusive() default "";

    public String minExclusiveExpression() default "";

    public String maxExclusive() default "";

    public String maxExclusiveExpression() default "";

    public String message() default "";

    public String key() default "";

    public String[] messageParams() default {};

    public String fieldName() default "";

    public boolean shortCircuit() default false;

    public ValidatorType type() default ValidatorType.FIELD;
}

