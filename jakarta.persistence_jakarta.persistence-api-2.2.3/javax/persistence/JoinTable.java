/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JoinTable {
    public String name() default "";

    public String catalog() default "";

    public String schema() default "";

    public JoinColumn[] joinColumns() default {};

    public JoinColumn[] inverseJoinColumns() default {};

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);

    public ForeignKey inverseForeignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);

    public UniqueConstraint[] uniqueConstraints() default {};

    public Index[] indexes() default {};
}

