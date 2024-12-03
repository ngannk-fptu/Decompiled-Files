/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ApiRestEntityFactory
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.instrumentation.SuppressValidationInstrumentation
 *  com.atlassian.graphql.spi.CombinedGraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  graphql.execution.instrumentation.Instrumentation
 *  graphql.validation.ValidationErrorType
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.confluence.core.ApiRestEntityFactory;
import com.atlassian.confluence.plugins.graphql.extensions.ConfluenceTypesExtension;
import com.atlassian.confluence.plugins.graphql.extensions.DenyAnonymousExtension;
import com.atlassian.confluence.plugins.graphql.extensions.EnrichmentExtension;
import com.atlassian.confluence.plugins.graphql.extensions.ExpansionFieldsExtension;
import com.atlassian.confluence.plugins.graphql.extensions.LimitRequestSizeExtension;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.instrumentation.SuppressValidationInstrumentation;
import com.atlassian.graphql.spi.CombinedGraphQLExtensions;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import graphql.execution.instrumentation.Instrumentation;
import graphql.validation.ValidationErrorType;
import java.util.List;
import java.util.Set;

@GraphQLExtensions
public class ConfluenceGraphQLExtensions
extends CombinedGraphQLExtensions {
    public ConfluenceGraphQLExtensions(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport ApiRestEntityFactory apiRestEntityFactory) {
        super((List)Lists.newArrayList((Object[])new com.atlassian.graphql.spi.GraphQLExtensions[]{new ConfluenceTypesExtension(pluginAccessor), new ExpansionFieldsExtension(), new EnrichmentExtension(apiRestEntityFactory), new DenyAnonymousExtension(), new LimitRequestSizeExtension()}));
    }

    public Instrumentation getInstrumentation() {
        return new SuppressValidationInstrumentation((Set)Sets.newHashSet((Object[])new ValidationErrorType[]{ValidationErrorType.FieldUndefined, ValidationErrorType.VariableTypeMismatch}));
    }
}

