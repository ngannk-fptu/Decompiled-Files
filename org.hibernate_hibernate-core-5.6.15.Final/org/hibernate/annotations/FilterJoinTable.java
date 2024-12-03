/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.FilterJoinTables;
import org.hibernate.annotations.SqlFragmentAlias;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=FilterJoinTables.class)
public @interface FilterJoinTable {
    public String name();

    public String condition() default "";

    public boolean deduceAliasInjectionPoints() default true;

    public SqlFragmentAlias[] aliases() default {};
}

