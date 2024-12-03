/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.types.DynamicType
 *  com.atlassian.graphql.types.DynamicTypeBuilder
 *  com.atlassian.graphql.utils.GraphQLSchemaMetadata
 *  com.google.common.collect.Lists
 *  graphql.schema.GraphQLOutputType
 *  graphql.schema.GraphQLType
 *  org.apache.commons.lang3.reflect.TypeUtils
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.graphql.types.ContentMetadataTypeBuilder;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.types.DynamicType;
import com.atlassian.graphql.types.DynamicTypeBuilder;
import com.atlassian.graphql.utils.GraphQLSchemaMetadata;
import com.google.common.collect.Lists;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.TypeUtils;

public class SpaceMetadataTypeBuilder
extends DynamicTypeBuilder {
    public SpaceMetadataTypeBuilder(GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        super(typeBuilder, extensions);
    }

    public String getTypeName(Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return "SpaceMetadata";
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        Member member = element instanceof Member ? (Member)((Object)element) : null;
        return member != null && member.getDeclaringClass() == Space.class && member.getName().equals("metadata");
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        DynamicType dynamicType = ContentMetadataTypeBuilder.makeDynamicType(typeName, SpaceMetadataTypeBuilder.getMetadataProperties());
        GraphQLType result = super.buildType(typeName, (Type)dynamicType, element, context);
        GraphQLSchemaMetadata.markAllFieldsExpandable((GraphQLOutputType)((GraphQLOutputType)result), (Map)context.getTypes(), (boolean)true);
        return result;
    }

    private static List<MetadataProperty> getMetadataProperties() {
        ParameterizedType labelsPropertyType = TypeUtils.parameterize(PageResponse.class, (Type[])new Type[]{Label.class});
        return Lists.newArrayList((Object[])new MetadataProperty[]{new MetadataProperty("labels", (Type)labelsPropertyType)});
    }
}

