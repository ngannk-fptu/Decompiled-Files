/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.Converts;

@Repeatable(value=Converts.class)
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Convert {
    public Class converter() default void.class;

    public String attributeName() default "";

    public boolean disableConversion() default false;
}

