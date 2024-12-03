/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumns;

@Repeatable(value=JoinColumns.class)
@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JoinColumn {
    public String name() default "";

    public String referencedColumnName() default "";

    public boolean unique() default false;

    public boolean nullable() default true;

    public boolean insertable() default true;

    public boolean updatable() default true;

    public String columnDefinition() default "";

    public String table() default "";

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);
}

