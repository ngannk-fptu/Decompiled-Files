/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.graphql.annotations;

import com.atlassian.graphql.annotations.GraphQLPossibleType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface GraphQLPossibleTypes {
    public GraphQLPossibleType[] value();
}

