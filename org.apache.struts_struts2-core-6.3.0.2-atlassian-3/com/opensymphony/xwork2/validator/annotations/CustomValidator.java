/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.annotations;

import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CustomValidator {
    public String type();

    public String fieldName() default "";

    public String message() default "";

    public String key() default "";

    public String[] messageParams() default {};

    public ValidationParameter[] parameters() default {};

    public boolean shortCircuit() default false;
}

