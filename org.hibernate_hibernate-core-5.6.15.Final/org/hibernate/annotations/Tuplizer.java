/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.EntityMode;
import org.hibernate.annotations.Tuplizers;

@Target(value={ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Tuplizers.class)
public @interface Tuplizer {
    public Class impl();

    @Deprecated
    public String entityMode() default "pojo";

    public EntityMode entityModeType() default EntityMode.POJO;
}

