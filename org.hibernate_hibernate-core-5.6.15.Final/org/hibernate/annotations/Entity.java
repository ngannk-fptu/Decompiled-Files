/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.PolymorphismType;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Deprecated
public @interface Entity {
    @Deprecated
    public boolean mutable() default true;

    @Deprecated
    public boolean dynamicInsert() default false;

    @Deprecated
    public boolean dynamicUpdate() default false;

    @Deprecated
    public boolean selectBeforeUpdate() default false;

    @Deprecated
    public PolymorphismType polymorphism() default PolymorphismType.IMPLICIT;

    @Deprecated
    public OptimisticLockType optimisticLock() default OptimisticLockType.VERSION;

    @Deprecated
    public String persister() default "";
}

