/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  graphql.Scalars
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.utils.ReflectionUtils;
import graphql.Scalars;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.function.Function;

public class JsonStringTypeBuilder
implements GraphQLTypeBuilder {
    public boolean canBuildType(Type type, AnnotatedElement element) {
        return ReflectionUtils.getClazz((Type)type) == JsonString.class;
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return Scalars.GraphQLString;
    }

    public Function<Object, Object> getValueTransformer(Type type, AnnotatedElement element) {
        if (ReflectionUtils.getClazz((Type)type).equals(JsonString.class)) {
            return str -> str != null ? ((JsonString)str).getValue() : null;
        }
        return null;
    }
}

