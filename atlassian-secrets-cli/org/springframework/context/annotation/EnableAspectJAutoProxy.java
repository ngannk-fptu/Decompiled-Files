/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.AspectJAutoProxyRegistrar;
import org.springframework.context.annotation.Import;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={AspectJAutoProxyRegistrar.class})
public @interface EnableAspectJAutoProxy {
    public boolean proxyTargetClass() default false;

    public boolean exposeProxy() default false;
}

