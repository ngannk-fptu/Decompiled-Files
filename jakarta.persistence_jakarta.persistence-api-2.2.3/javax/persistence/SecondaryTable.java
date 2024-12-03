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
import javax.persistence.Index;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTables;
import javax.persistence.UniqueConstraint;

@Repeatable(value=SecondaryTables.class)
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SecondaryTable {
    public String name();

    public String catalog() default "";

    public String schema() default "";

    public PrimaryKeyJoinColumn[] pkJoinColumns() default {};

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);

    public UniqueConstraint[] uniqueConstraints() default {};

    public Index[] indexes() default {};
}

