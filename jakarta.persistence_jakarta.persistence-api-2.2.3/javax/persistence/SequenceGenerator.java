/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.SequenceGenerators;

@Repeatable(value=SequenceGenerators.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SequenceGenerator {
    public String name();

    public String sequenceName() default "";

    public String catalog() default "";

    public String schema() default "";

    public int initialValue() default 1;

    public int allocationSize() default 50;
}

