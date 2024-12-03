/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.AssociationOverrides;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

@Repeatable(value=AssociationOverrides.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface AssociationOverride {
    public String name();

    public JoinColumn[] joinColumns() default {};

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);

    public JoinTable joinTable() default @JoinTable;
}

