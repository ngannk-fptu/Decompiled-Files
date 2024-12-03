/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ConstraintMode;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    public String name() default "";

    public ConstraintMode value() default ConstraintMode.CONSTRAINT;

    public String foreignKeyDefinition() default "";
}

