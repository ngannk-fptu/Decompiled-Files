/*
 * Decompiled with CFR 0.152.
 */
package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface WebResult {
    public String name() default "";

    public String partName() default "";

    public String targetNamespace() default "";

    public boolean header() default false;
}

