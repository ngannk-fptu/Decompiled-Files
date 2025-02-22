/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.graphql.annotations.expansions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface GraphQLExpandable {
    public String value() default "";

    public boolean skip() default false;
}

