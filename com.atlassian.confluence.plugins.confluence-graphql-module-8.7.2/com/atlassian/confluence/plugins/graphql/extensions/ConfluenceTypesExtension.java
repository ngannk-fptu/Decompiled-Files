/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.graphql.json.jersey.JerseyResourceMethodExtensions
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.types.OptionalTypeBuilder
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.confluence.plugins.graphql.types.CollapsableListTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.ContainerTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.ContentMetadataTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.EnumKeyMapTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.JavaTimeTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.JsonStringTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.PageResponseTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.ReferenceTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.SpaceMetadataTypeBuilder;
import com.atlassian.confluence.plugins.graphql.types.UserTypeBuilder;
import com.atlassian.fugue.Option;
import com.atlassian.graphql.json.jersey.JerseyResourceMethodExtensions;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.types.OptionalTypeBuilder;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import java.util.List;

public class ConfluenceTypesExtension
extends JerseyResourceMethodExtensions {
    private PluginAccessor pluginAccessor;

    public ConfluenceTypesExtension(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public List<GraphQLTypeBuilder> getAdditionalTypeBuilders(GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        return Lists.newArrayList((Object[])new GraphQLTypeBuilder[]{new ReferenceTypeBuilder(typeBuilder), new OptionalTypeBuilder(typeBuilder, Option.class, option -> option.getOrElse(null)), new JsonStringTypeBuilder(), new EnumKeyMapTypeBuilder(typeBuilder, extensions), new ContainerTypeBuilder(typeBuilder, extensions), new UserTypeBuilder(typeBuilder, extensions), new PageResponseTypeBuilder(typeBuilder), new ContentMetadataTypeBuilder(this.pluginAccessor, typeBuilder, extensions), new SpaceMetadataTypeBuilder(typeBuilder, extensions), new CollapsableListTypeBuilder(typeBuilder), new JavaTimeTypeBuilder()});
    }
}

