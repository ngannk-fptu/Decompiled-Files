/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface AuthenticationPrincipal {
    public boolean errorOnInvalidType() default false;

    public String expression() default "";
}

