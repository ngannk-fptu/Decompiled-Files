/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.FIELD})
public @interface DescriptorKey {
    public String value();

    public boolean omitIfDefault() default false;
}

