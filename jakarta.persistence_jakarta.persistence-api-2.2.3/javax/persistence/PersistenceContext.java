/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceContexts;
import javax.persistence.PersistenceProperty;
import javax.persistence.SynchronizationType;

@Repeatable(value=PersistenceContexts.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PersistenceContext {
    public String name() default "";

    public String unitName() default "";

    public PersistenceContextType type() default PersistenceContextType.TRANSACTION;

    public SynchronizationType synchronization() default SynchronizationType.SYNCHRONIZED;

    public PersistenceProperty[] properties() default {};
}

