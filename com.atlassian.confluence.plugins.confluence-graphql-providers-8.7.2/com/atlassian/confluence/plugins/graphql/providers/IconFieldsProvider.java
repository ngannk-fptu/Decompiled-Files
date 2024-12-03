/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLNonNull
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  graphql.schema.DataFetchingEnvironment
 *  graphql.schema.GraphQLFieldDefinition
 *  javax.ws.rs.DefaultValue
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLNonNull;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.DefaultValue;

@AnonymousAllowed
@GraphQLExtensions
public final class IconFieldsProvider
implements GraphQLTypeContributor {
    private final ContextPathHolder contextPathHolder;
    private final GlobalSettingsManager settingsManager;

    public IconFieldsProvider(@ComponentImport ContextPathHolder contextPathHolder, @ComponentImport GlobalSettingsManager settingsManager) {
        this.contextPathHolder = contextPathHolder;
        this.settingsManager = settingsManager;
    }

    public String contributeTypeName(String typeName, Type type, GraphQLTypeBuilderContext context) {
        return null;
    }

    public void contributeFields(String typeName, Type type, List<GraphQLFieldDefinition> fields, GraphQLTypeBuilderContext context) {
        if (!Objects.equals(typeName, "Icon")) {
            return;
        }
        fields.addAll(context.buildProviderGraphQLType("query", (Object)this).getFieldDefinitions());
    }

    @GraphQLName(value="path")
    @GraphQLNonNull
    public String getPath(DataFetchingEnvironment env, @GraphQLName(value="type") @DefaultValue(value="RELATIVE_NO_CONTEXT") PathType type) {
        String originalPath = this.getOriginalPath(env);
        switch (type) {
            case ABSOLUTE: {
                return this.settingsManager.getGlobalSettings().getBaseUrl() + originalPath;
            }
            case RELATIVE: {
                return this.contextPathHolder.getContextPath() + originalPath;
            }
        }
        return originalPath;
    }

    private String getOriginalPath(DataFetchingEnvironment env) {
        Object source = env.getSource();
        if (source instanceof Map) {
            return String.valueOf(((Map)env.getSource()).get("path"));
        }
        if (source instanceof Icon) {
            return ((Icon)env.getSource()).getPath();
        }
        throw new IllegalArgumentException("Unexpected source type: " + source);
    }

    public static enum PathType {
        ABSOLUTE,
        RELATIVE,
        RELATIVE_NO_CONTEXT;

    }
}

