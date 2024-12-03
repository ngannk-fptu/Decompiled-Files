/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 *  javax.validation.constraints.Pattern
 *  javax.validation.constraintvalidation.SupportedValidationTarget
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.constraints.br;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.constraints.Mod11Check;

@Pattern(regexp="[0-9]{12}")
@Mod11Check.List(value={@Mod11Check(threshold=9, endIndex=7, checkDigitIndex=10), @Mod11Check(threshold=9, startIndex=8, endIndex=10, checkDigitIndex=11)})
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy={})
@SupportedValidationTarget(value={ValidationTarget.ANNOTATED_ELEMENT})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
public @interface TituloEleitoral {
    public String message() default "{org.hibernate.validator.constraints.br.TituloEleitoral.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public TituloEleitoral[] value();
    }
}

