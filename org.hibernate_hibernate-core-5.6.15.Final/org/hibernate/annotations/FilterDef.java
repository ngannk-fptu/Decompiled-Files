/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=FilterDefs.class)
public @interface FilterDef {
    public String name();

    public String defaultCondition() default "";

    public ParamDef[] parameters() default {};
}

