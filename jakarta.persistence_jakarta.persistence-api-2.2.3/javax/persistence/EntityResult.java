/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.FieldResult;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface EntityResult {
    public Class entityClass();

    public FieldResult[] fields() default {};

    public String discriminatorColumn() default "";
}

