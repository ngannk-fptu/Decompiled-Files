/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.types.DynamicType
 *  com.atlassian.graphql.types.DynamicTypeBuilder
 *  com.atlassian.graphql.utils.GraphQLSchemaMetadata
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.collect.Sets
 *  graphql.schema.GraphQLOutputType
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.types.DynamicType;
import com.atlassian.graphql.types.DynamicTypeBuilder;
import com.atlassian.graphql.utils.GraphQLSchemaMetadata;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.collect.Sets;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentMetadataTypeBuilder
extends DynamicTypeBuilder {
    private static final Set<String> EXCLUDED_METADATA_PROPERTIES = Sets.newHashSet((Object[])new String[]{"properties"});
    private final PluginAccessor pluginAccessor;

    public ContentMetadataTypeBuilder(PluginAccessor pluginAccessor, GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        super(typeBuilder, extensions);
        this.pluginAccessor = pluginAccessor;
    }

    public String getTypeName(Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return "ContentMetadata";
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        Member member = element instanceof Member ? (Member)((Object)element) : null;
        return member != null && member.getDeclaringClass() == Content.class && member.getName().equals("metadata");
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        List providers;
        List list = providers = this.pluginAccessor != null ? this.pluginAccessor.getEnabledModulesByClass(ModelMetadataProvider.class) : Collections.emptyList();
        if (providers.isEmpty()) {
            return null;
        }
        HashMap<String, Type> fieldTypes = new HashMap<String, Type>();
        for (ModelMetadataProvider provider : providers) {
            Map<String, Type> metadataSchema = ContentMetadataTypeBuilder.getMetadataSchema(provider);
            if (metadataSchema == null) continue;
            fieldTypes.putAll(metadataSchema);
        }
        DynamicType dynamicType = new DynamicType(typeName, fieldTypes);
        GraphQLType result = super.buildType(typeName, (Type)dynamicType, element, context);
        GraphQLSchemaMetadata.markAllFieldsExpandable((GraphQLOutputType)((GraphQLOutputType)result), (Map)context.getTypes(), (boolean)true);
        return result;
    }

    private static Map<String, Type> getMetadataSchema(ModelMetadataProvider provider) {
        List<MetadataProperty> properties = provider.getProperties();
        if (properties == null) {
            return Collections.emptyMap();
        }
        properties = properties.stream().filter(property -> !EXCLUDED_METADATA_PROPERTIES.contains(property.getPropertyName())).collect(Collectors.toList());
        DynamicType type = ContentMetadataTypeBuilder.makeDynamicType("ContentMetadata_" + provider.getClass().getSimpleName(), properties);
        return type.getFieldTypes();
    }

    static DynamicType makeDynamicType(String typeName, List<MetadataProperty> fields) {
        HashMap<String, Type> fieldMap = new HashMap<String, Type>();
        for (MetadataProperty field : fields) {
            Type type = field.getPropertyType() != null ? field.getPropertyType() : ContentMetadataTypeBuilder.makeDynamicType(typeName + "_" + field.getPropertyName(), field.getChildren());
            fieldMap.put(field.getPropertyName(), type);
        }
        return new DynamicType(typeName, fieldMap);
    }
}

