/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.AnyMetaDefs;
import org.hibernate.annotations.MetaValue;

@Deprecated
@Target(value={ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=AnyMetaDefs.class)
public @interface AnyMetaDef {
    public String name() default "";

    public String metaType();

    public String idType();

    public MetaValue[] metaValues();
}

