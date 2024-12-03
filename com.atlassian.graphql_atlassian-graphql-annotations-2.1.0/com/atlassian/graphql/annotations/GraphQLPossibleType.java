/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.graphql.annotations;

import com.atlassian.graphql.annotations.GraphQLPossibleTypes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=GraphQLPossibleTypes.class)
public @interface GraphQLPossibleType {
    public Class[] value();
}

