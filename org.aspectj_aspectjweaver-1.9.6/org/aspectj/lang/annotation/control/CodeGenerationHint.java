/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.annotation.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.METHOD})
public @interface CodeGenerationHint {
    public String ifNameSuffix() default "";
}

