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
public @interface Mod11Check {
    public String message() default "{org.hibernate.validator.constraints.Mod11Check.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public int threshold() default 0x7FFFFFFF;

    public int startIndex() default 0;

    public int endIndex() default 0x7FFFFFFF;

    public int checkDigitIndex() default -1;

    public boolean ignoreNonDigitCharacters() default false;

    public char treatCheck10As() default 88;

    public char treatCheck11As() default 48;

    public ProcessingDirection processingDirection() default ProcessingDirection.RIGHT_TO_LEFT;

    public static enum ProcessingDirection {
        RIGHT_TO_LEFT,
        LEFT_TO_RIGHT;

    }

    @Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public Mod11Check[] value();
    }
}

