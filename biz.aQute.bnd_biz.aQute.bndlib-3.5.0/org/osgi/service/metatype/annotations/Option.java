/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.metatype.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={})
public @interface Option {
    public String label() default "";

    public String value();
}

