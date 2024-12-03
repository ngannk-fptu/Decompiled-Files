/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.Index;
import javax.persistence.TableGenerators;
import javax.persistence.UniqueConstraint;

@Repeatable(value=TableGenerators.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TableGenerator {
    public String name();

    public String table() default "";

    public String catalog() default "";

    public String schema() default "";

    public String pkColumnName() default "";

    public String valueColumnName() default "";

    public String pkColumnValue() default "";

    public int initialValue() default 0;

    public int allocationSize() default 50;

    public UniqueConstraint[] uniqueConstraints() default {};

    public Index[] indexes() default {};
}

