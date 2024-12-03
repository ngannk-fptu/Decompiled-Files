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
@Target(value={ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
public @interface ParameterScriptAssert {
    public String message() default "{org.hibernate.validator.constraints.ParametersScriptAssert.message}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public String lang();

    public String script();

    @Target(value={ElementType.CONSTRUCTOR, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public ParameterScriptAssert[] value();
    }
}

