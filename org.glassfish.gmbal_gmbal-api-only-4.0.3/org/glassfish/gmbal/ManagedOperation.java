/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.gmbal.Impact;

@Documented
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ManagedOperation {
    public String id() default "";

    public Impact impact() default Impact.UNKNOWN;
}

