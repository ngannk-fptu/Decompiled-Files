/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDefs;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=TypeDefs.class)
public @interface TypeDef {
    public String name() default "";

    public Class<?> typeClass();

    public Class<?> defaultForType() default void.class;

    public Parameter[] parameters() default {};
}

