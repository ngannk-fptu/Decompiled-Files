/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.GenericGenerators;
import org.hibernate.annotations.Parameter;

@Target(value={ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=GenericGenerators.class)
public @interface GenericGenerator {
    public String name();

    public String strategy();

    public Parameter[] parameters() default {};
}

