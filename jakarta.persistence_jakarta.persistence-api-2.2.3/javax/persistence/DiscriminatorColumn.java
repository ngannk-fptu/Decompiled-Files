/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.DiscriminatorType;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface DiscriminatorColumn {
    public String name() default "DTYPE";

    public DiscriminatorType discriminatorType() default DiscriminatorType.STRING;

    public String columnDefinition() default "";

    public int length() default 31;
}

