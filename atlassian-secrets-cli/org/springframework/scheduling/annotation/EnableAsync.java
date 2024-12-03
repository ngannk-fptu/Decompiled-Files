/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurationSelector;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={AsyncConfigurationSelector.class})
public @interface EnableAsync {
    public Class<? extends Annotation> annotation() default Annotation.class;

    public boolean proxyTargetClass() default false;

    public AdviceMode mode() default AdviceMode.PROXY;

    public int order() default 0x7FFFFFFF;
}

