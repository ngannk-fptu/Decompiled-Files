/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  graphql.schema.GraphQLFieldDefinition
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import graphql.schema.GraphQLFieldDefinition;
import java.lang.reflect.Type;
import java.util.List;

@GraphQLExtensions
public class UserFieldsProvider
implements GraphQLTypeContributor {
    private final PersonService personService;

    public UserFieldsProvider(@ComponentImport PersonService personService) {
        this.personService = personService;
    }

    public String contributeTypeName(String typeName, Type type, GraphQLTypeBuilderContext context) {
        return null;
    }

    public void contributeFields(String typeName, Type type, List<GraphQLFieldDefinition> fields, GraphQLTypeBuilderContext context) {
    }
}

