/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.LoadTimeWeavingConfiguration;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={LoadTimeWeavingConfiguration.class})
public @interface EnableLoadTimeWeaving {
    public AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;

    public static enum AspectJWeaving {
        ENABLED,
        DISABLED,
        AUTODETECT;

    }
}

