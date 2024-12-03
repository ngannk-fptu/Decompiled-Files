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
import javax.persistence.MapKeyJoinColumn;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MapKeyJoinColumns {
    public MapKeyJoinColumn[] value();

    public ForeignKey foreignKey() default @ForeignKey(value=ConstraintMode.PROVIDER_DEFAULT);
}

