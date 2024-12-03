/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.atlassian.graphql.utils.GraphQLUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.base.Charsets
 *  com.google.common.base.Strings
 *  com.google.common.io.CharStreams
 *  graphql.schema.GraphQLFieldDefinition
 *  graphql.schema.GraphQLFieldsContainer
 *  graphql.schema.GraphQLObjectType
 *  graphql.schema.GraphQLOutputType
 *  graphql.schema.GraphQLType
 *  graphql.schema.idl.SchemaPrinter
 *  graphql.schema.idl.SchemaPrinter$Options
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.graphql.resource;

import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.plugins.graphql.resource.ConfluenceGraphQLRestEndpoint;
import com.atlassian.graphql.utils.GraphQLUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.idl.SchemaPrinter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Qualifier;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="")
public class GraphResource {
    private final ConfluenceGraphQLRestEndpoint endpoint;
    private final DarkFeatureManager darkFeatureManager;
    private final SettingsService settingsService;

    public GraphResource(ConfluenceGraphQLRestEndpoint endpoint, @ComponentImport @Qualifier(value="darkFeatureManager") DarkFeatureManager darkFeatureManager, @ComponentImport SettingsService settingsService) {
        this.endpoint = endpoint;
        this.darkFeatureManager = darkFeatureManager;
        this.settingsService = settingsService;
    }

    @POST
    @Path(value="")
    public Response query(String requestString, @QueryParam(value="query") String query, @Context UriInfo uriInfo, @Context HttpServletRequest request) throws Exception {
        if (!this.darkFeatureManager.isEnabledForCurrentUser("graphql").orElse(false).booleanValue()) {
            return Response.status((int)404).build();
        }
        return Response.ok((Object)this.endpoint.execute(requestString, query, uriInfo, request)).build();
    }

    @GET
    @Produces(value={"text/html"})
    @Path(value="")
    public Response getGraphiqlApp() throws Exception {
        if (!this.darkFeatureManager.isEnabledForCurrentUser("graphiql").orElse(false).booleanValue()) {
            return Response.status((int)404).build();
        }
        InputStream templateResource = GraphResource.class.getClassLoader().getResourceAsStream("graphiql.html");
        String template = CharStreams.toString((Readable)new InputStreamReader(templateResource, Charsets.UTF_8));
        template = template.replace("{basePath}", this.settingsService.getGlobalSettings().getBaseUrl());
        return Response.ok((Object)template).build();
    }

    @GET
    @Path(value="_schema")
    public Response getSchema() {
        return this.getSchema(null, null);
    }

    @GET
    @Path(value="_schema/{field}")
    public Response getSchema(@PathParam(value="field") String field) {
        return this.getSchema(field, null);
    }

    @GET
    @Path(value="_schema/{field1}/{field2}")
    public Response getSchema(@PathParam(value="field1") String field1, @PathParam(value="field2") String field2) {
        GraphQLObjectType type = this.endpoint.getSchema().getQueryType();
        if (!Strings.isNullOrEmpty((String)field1)) {
            type = this.navigateToFieldType((GraphQLOutputType)type, field1);
        }
        if (!Strings.isNullOrEmpty((String)field2)) {
            type = this.navigateToFieldType((GraphQLOutputType)type, field2);
        }
        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions().includeScalarTypes(true).includeDirectiveDefinitions(false).includeDirectives(true);
        SchemaPrinter printer = new SchemaPrinter(options);
        return Response.ok((Object)(type == null ? "" : printer.print(this.endpoint.getSchema()))).build();
    }

    private GraphQLOutputType navigateToFieldType(GraphQLOutputType type, String fieldName) {
        GraphQLFieldDefinition fieldDefinition;
        if (type == null) {
            return null;
        }
        if ((type = (GraphQLOutputType)GraphQLUtils.unwrap((GraphQLType)type, Collections.emptyMap())) instanceof GraphQLFieldsContainer && (fieldDefinition = ((GraphQLFieldsContainer)type).getFieldDefinition(fieldName)) != null) {
            return fieldDefinition.getType();
        }
        return null;
    }
}

