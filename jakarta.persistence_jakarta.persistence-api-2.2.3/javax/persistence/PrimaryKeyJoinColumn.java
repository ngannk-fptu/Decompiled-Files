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
import javax.persistence.PrimaryKeyJoinColumns;

@Repeatable(value=PrimaryKeyJoinColumns.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PrimaryKeyJoinColumn {
    public String name() default "";

    public String referencedColumnName() default "";

    public String columnDefinition() default "";

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);
}

