/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  graphql.Scalars
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import graphql.Scalars;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class JavaTimeTypeBuilder
implements GraphQLTypeBuilder {
    public boolean canBuildType(Type type, AnnotatedElement element) {
        return this.getScalarType(type) != null;
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return this.getScalarType(type);
    }

    private GraphQLType getScalarType(Type type) {
        if (type == OffsetDateTime.class) {
            return Scalars.GraphQLString;
        }
        return null;
    }
}

