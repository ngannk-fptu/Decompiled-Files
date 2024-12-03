/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ApiRestEntityFactory
 *  com.atlassian.confluence.core.ApiRestEntityFactory$SchemaType
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.graphql.datafetcher.FieldDataFetcher
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  graphql.schema.DataFetcher
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  graphql.schema.GraphQLOutputType
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.confluence.core.ApiRestEntityFactory;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.graphql.datafetcher.FieldDataFetcher;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.utils.ReflectionUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class EnrichmentExtension
implements GraphQLExtensions {
    private ApiRestEntityFactory apiRestEntityFactory;

    public EnrichmentExtension(@ComponentImport ApiRestEntityFactory apiRestEntityFactory) {
        this.apiRestEntityFactory = Objects.requireNonNull(apiRestEntityFactory);
    }

    public String contributeTypeName(String typeName, Type type, GraphQLTypeBuilderContext context) {
        return null;
    }

    public void contributeFields(String typeName, Type type, List<GraphQLFieldDefinition> fields, GraphQLTypeBuilderContext context) {
        Class clazz = ReflectionUtils.getClazz((Type)type);
        boolean isRoot = this.apiRestEntityFactory.isEnrichableEntity(clazz) || this.apiRestEntityFactory.isEnrichableList(clazz);
        Map propertyTypes = this.apiRestEntityFactory.getEnrichedPropertyTypes(type, isRoot);
        for (Map.Entry property : propertyTypes.entrySet()) {
            String propertyName = (String)property.getKey();
            Type propertyType = (Type)property.getValue();
            context.enterField(propertyName, propertyType);
            GraphQLOutputType graphFieldType = (GraphQLOutputType)context.getTypeBuilder().buildType((Type)property.getValue(), null, context);
            context.exitField();
            if (graphFieldType == null) continue;
            GraphQLFieldDefinition fieldDefinition = GraphQLFieldDefinition.newFieldDefinition().name((String)property.getKey()).type(graphFieldType).dataFetcher(EnrichmentExtension.createPropertyDataFetcher(context.getTypeBuilder(), propertyName, propertyType)).build();
            fields.add(fieldDefinition);
        }
    }

    private static DataFetcher createPropertyDataFetcher(GraphQLTypeBuilder typeBuilder, String propertyName, Type fieldType) {
        Function valueTransformer = typeBuilder.getValueTransformer(fieldType, null);
        FieldDataFetcher dataFetcher = new FieldDataFetcher(propertyName, null);
        return valueTransformer != null ? arg_0 -> EnrichmentExtension.lambda$createPropertyDataFetcher$0(valueTransformer, (DataFetcher)dataFetcher, arg_0) : dataFetcher;
    }

    public Function<Object, Object> getValueTransformer(Type type, AnnotatedElement element) {
        return obj -> {
            HashMap result = obj;
            if (element instanceof Method) {
                result = this.apiRestEntityFactory.convertAndEnrich(obj, ApiRestEntityFactory.SchemaType.GRAPHQL);
            }
            if (result instanceof RestEntity) {
                result = new HashMap(((RestEntity)result).properties());
            }
            return result;
        };
    }

    private static /* synthetic */ Object lambda$createPropertyDataFetcher$0(Function valueTransformer, DataFetcher dataFetcher, DataFetchingEnvironment env) throws Exception {
        return valueTransformer.apply(dataFetcher.get(env));
    }
}

