/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.Payload
 */
package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy={})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
public @interface Mod10Check {
    public String message() default "{org.hibernate.validator.constraints.Mod10Check.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public int multiplier() default 3;

    public int weight() default 1;

    public int startIndex() default 0;

    public int endIndex() default 0x7FFFFFFF;

    public int checkDigitIndex() default -1;

    public boolean ignoreNonDigitCharacters() default true;

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public Mod10Check[] value();
    }
}

