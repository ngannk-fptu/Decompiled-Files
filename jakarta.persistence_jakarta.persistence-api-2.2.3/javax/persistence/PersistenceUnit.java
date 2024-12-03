/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.PersistenceUnits;

@Repeatable(value=PersistenceUnits.class)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PersistenceUnit {
    public String name() default "";

    public String unitName() default "";
}

