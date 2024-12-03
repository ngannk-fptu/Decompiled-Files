/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.OverridesAttribute
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 *  javax.validation.constraints.Pattern
 *  javax.validation.constraints.Pattern$Flag
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
import javax.validation.constraints.Pattern;

@Documented
@Constraint(validatedBy={})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
@ReportAsSingleViolation
@Pattern(regexp="")
@Deprecated
public @interface Email {
    public String message() default "{org.hibernate.validator.constraints.Email.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint=Pattern.class, name="regexp")
    public String regexp() default ".*";

    @OverridesAttribute(constraint=Pattern.class, name="flags")
    public Pattern.Flag[] flags() default {};

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public Email[] value();
    }
}

