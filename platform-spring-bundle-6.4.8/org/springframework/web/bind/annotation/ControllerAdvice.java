/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {
    @AliasFor(value="basePackages")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] basePackages() default {};

    public Class<?>[] basePackageClasses() default {};

    public Class<?>[] assignableTypes() default {};

    public Class<? extends Annotation>[] annotations() default {};
}

