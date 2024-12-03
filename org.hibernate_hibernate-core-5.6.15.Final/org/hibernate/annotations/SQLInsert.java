/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.ResultCheckStyle;

@Target(value={ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SQLInsert {
    public String sql();

    public boolean callable() default false;

    public ResultCheckStyle check() default ResultCheckStyle.NONE;
}

