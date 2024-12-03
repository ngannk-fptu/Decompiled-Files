/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ParameterMode;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface StoredProcedureParameter {
    public String name() default "";

    public ParameterMode mode() default ParameterMode.IN;

    public Class type();
}

