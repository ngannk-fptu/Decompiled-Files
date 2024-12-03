/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.Index;
import javax.persistence.UniqueConstraint;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Table {
    public String name() default "";

    public String catalog() default "";

    public String schema() default "";

    public UniqueConstraint[] uniqueConstraints() default {};

    public Index[] indexes() default {};
}

