/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMappings;

@Repeatable(value=SqlResultSetMappings.class)
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SqlResultSetMapping {
    public String name();

    public EntityResult[] entities() default {};

    public ConstructorResult[] classes() default {};

    public ColumnResult[] columns() default {};
}

