/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface OrderColumn {
    public String name() default "";

    public boolean nullable() default true;

    public boolean insertable() default true;

    public boolean updatable() default true;

    public String columnDefinition() default "";
}

