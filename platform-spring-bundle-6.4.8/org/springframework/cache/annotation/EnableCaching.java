/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cache.annotation.CachingConfigurationSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={CachingConfigurationSelector.class})
public @interface EnableCaching {
    public boolean proxyTargetClass() default false;

    public AdviceMode mode() default AdviceMode.PROXY;

    public int order() default 0x7FFFFFFF;
}

