/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ManyToOne {
    public Class targetEntity() default void.class;

    public CascadeType[] cascade() default {};

    public FetchType fetch() default FetchType.EAGER;

    public boolean optional() default true;
}

