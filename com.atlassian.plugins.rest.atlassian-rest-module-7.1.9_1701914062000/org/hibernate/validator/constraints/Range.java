/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.OverridesAttribute
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 *  javax.validation.constraints.Max
 *  javax.validation.constraints.Min
 *  javax.validation.constraintvalidation.SupportedValidationTarget
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@Documented
@Constraint(validatedBy={})
@SupportedValidationTarget(value={ValidationTarget.ANNOTATED_ELEMENT})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
@Min(value=0L)
@Max(value=0x7FFFFFFFFFFFFFFFL)
@ReportAsSingleViolation
public @interface Range {
    @OverridesAttribute(constraint=Min.class, name="value")
    public long min() default 0L;

    @OverridesAttribute(constraint=Max.class, name="value")
    public long max() default 0x7FFFFFFFFFFFFFFFL;

    public String message() default "{org.hibernate.validator.constraints.Range.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public Range[] value();
    }
}

