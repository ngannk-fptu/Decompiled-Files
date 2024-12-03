/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.graphql.json.types.JsonObjectTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.graphql.json.types.JsonObjectTypeBuilder;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class ContainerTypeBuilder
extends JsonObjectTypeBuilder {
    public ContainerTypeBuilder(GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        super(typeBuilder, extensions);
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        return type == Container.class;
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return super.buildFromTypes(typeName, new Type[]{Content.class, Space.class}, element, context);
    }
}

