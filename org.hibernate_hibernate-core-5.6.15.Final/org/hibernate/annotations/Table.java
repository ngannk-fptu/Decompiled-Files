/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.Tables;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Tables.class)
public @interface Table {
    public String appliesTo();

    public Index[] indexes() default {};

    public String comment() default "";

    public ForeignKey foreignKey() default @ForeignKey(name="");

    public FetchMode fetch() default FetchMode.JOIN;

    public boolean inverse() default false;

    public boolean optional() default true;

    public SQLInsert sqlInsert() default @SQLInsert(sql="");

    public SQLUpdate sqlUpdate() default @SQLUpdate(sql="");

    public SQLDelete sqlDelete() default @SQLDelete(sql="");
}

