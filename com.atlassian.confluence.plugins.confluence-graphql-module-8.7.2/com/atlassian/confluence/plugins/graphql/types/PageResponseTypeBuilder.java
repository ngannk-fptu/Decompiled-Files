/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPageResponse
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPageResponse;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.utils.ReflectionUtils;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class PageResponseTypeBuilder
implements GraphQLTypeBuilder {
    private final GraphQLTypeBuilder typeBuilder;

    public PageResponseTypeBuilder(GraphQLTypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    public String getTypeName(Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        ParameterizedType paginationType = ReflectionUtils.createParameterizedType(GraphQLPageResponse.class, (ParameterizedType)((ParameterizedType)type));
        return this.typeBuilder.getTypeName((Type)paginationType, element, context);
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        Class clazz = ReflectionUtils.getClazz((Type)type);
        return type instanceof ParameterizedType && PageResponse.class.isAssignableFrom(clazz) && !GraphQLPageResponse.class.isAssignableFrom(clazz);
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        ParameterizedType paginationType = ReflectionUtils.createParameterizedType(GraphQLPageResponse.class, (ParameterizedType)((ParameterizedType)type));
        return (GraphQLType)context.updateFieldType((Type)paginationType, () -> this.typeBuilder.buildType(typeName, paginationType, element, context));
    }

    public Function<Object, Object> getValueTransformer(Type type, AnnotatedElement element) {
        return obj -> {
            PageResponse pageResponse = (PageResponse)obj;
            if (pageResponse == null) {
                return null;
            }
            int start = pageResponse.getPageRequest() != null ? pageResponse.getPageRequest().getStart() : 0;
            return new GraphQLPageResponse(pageResponse, (item, i) -> Integer.toString(start + (Integer)i));
        };
    }
}

