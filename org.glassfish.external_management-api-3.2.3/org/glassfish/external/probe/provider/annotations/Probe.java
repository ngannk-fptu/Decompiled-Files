/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.probe.provider.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Probe {
    public String name() default "";

    public boolean hidden() default false;

    public boolean self() default false;

    public String providerName() default "";

    public String moduleName() default "";

    public boolean stateful() default false;

    public String profileNames() default "";

    public boolean statefulReturn() default false;

    public boolean statefulException() default false;
}

