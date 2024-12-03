/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.SeeOtherException
 *  com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent
 *  com.atlassian.confluence.rest.api.model.ExceptionConverter$Server
 *  com.atlassian.confluence.rest.api.model.RestError
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.graphql.GraphQLContext
 *  com.atlassian.graphql.json.jersey.GraphQLRestException
 *  com.atlassian.graphql.json.types.JsonRootGraphQLTypeBuilder
 *  com.atlassian.graphql.rest.GraphQLRestRequest
 *  com.atlassian.graphql.rest.GraphQLRestServer
 *  com.atlassian.graphql.spi.GraphQLProviders
 *  com.atlassian.graphql.utils.AsyncExecutionStrategyIgnoreUndefinedFields
 *  com.atlassian.graphql.utils.GraphQLQueryCache
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.google.common.base.Strings
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableMap
 *  graphql.ExecutionInput
 *  graphql.GraphQLError
 *  graphql.execution.ExecutionStrategy
 *  graphql.execution.preparsed.PreparsedDocumentEntry
 *  graphql.language.Document
 *  graphql.parser.Parser
 *  graphql.schema.GraphQLSchema
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriInfo
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.graphql.resource;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.SeeOtherException;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.plugins.graphql.GraphQLQueryLimiter;
import com.atlassian.confluence.rest.api.model.ExceptionConverter;
import com.atlassian.confluence.rest.api.model.RestError;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.graphql.GraphQLContext;
import com.atlassian.graphql.json.jersey.GraphQLRestException;
import com.atlassian.graphql.json.types.JsonRootGraphQLTypeBuilder;
import com.atlassian.graphql.rest.GraphQLRestRequest;
import com.atlassian.graphql.rest.GraphQLRestServer;
import com.atlassian.graphql.spi.GraphQLProviders;
import com.atlassian.graphql.utils.AsyncExecutionStrategyIgnoreUndefinedFields;
import com.atlassian.graphql.utils.GraphQLQueryCache;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import graphql.ExecutionInput;
import graphql.GraphQLError;
import graphql.execution.ExecutionStrategy;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.language.Document;
import graphql.parser.Parser;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceGraphQLRestEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceGraphQLRestEndpoint.class);
    private static final Parser PARSER = new Parser();
    private final PluginAccessor pluginAccessor;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private GraphQLRestServer endpoint;
    private final GraphQLQueryCache cache = new GraphQLQueryCache(200000){

        protected void put(String query, Document queryDocument) {
            super.put("sha256:" + Hex.encodeHexString((byte[])DigestUtils.sha256((String)query)), queryDocument);
            super.put(query, queryDocument);
        }
    };

    @Autowired
    public ConfluenceGraphQLRestEndpoint(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport EventListenerRegistrar eventListenerRegistrar, @ComponentImport TransactionalExecutorFactory transactionalExecutorFactory) {
        this.pluginAccessor = pluginAccessor;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
    }

    @PostConstruct
    public void init() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(PluginFrameworkStartedEvent event) {
        this.getEndpoint();
    }

    @EventListener
    public void handleEvent(PluginEnabledEvent event) {
        this.invalidateEndpoint();
    }

    @EventListener
    public void handleEvent(PluginDisabledEvent event) {
        this.invalidateEndpoint();
    }

    @EventListener
    public void handleEvent(PluginUpgradedEvent event) {
        this.invalidateEndpoint();
    }

    public GraphQLSchema getSchema() {
        return this.getEndpoint().getSchema();
    }

    public Object execute(String requestString, @QueryParam(value="query") String query, @Context UriInfo uriInfo, @Context HttpServletRequest request) throws Exception {
        AtomicReference result = new AtomicReference();
        try {
            this.transactionalExecutorFactory.create().execute(conn -> {
                try {
                    result.set(this.executeNoTransaction(requestString, query, uriInfo, request));
                }
                catch (IOException ex) {
                    throw Throwables.propagate((Throwable)ex);
                }
                return null;
            });
        }
        catch (Exception ex) {
            if (result.get() != null && ex.getClass().getSimpleName().equals("UnexpectedRollbackException")) {
                return result.get();
            }
            throw ex;
        }
        return result.get();
    }

    private Object executeNoTransaction(String requestString, @QueryParam(value="query") String query, @Context UriInfo uriInfo, @Context HttpServletRequest request) throws IOException {
        GraphQLContext context = new GraphQLContext();
        context.injectParameterValue(UriInfo.class, (Object)uriInfo);
        context.injectParameterValue(HttpServletRequest.class, (Object)request);
        return this.getEndpoint().execute(query != null ? query : requestString, context);
    }

    private synchronized void invalidateEndpoint() {
        this.endpoint = null;
    }

    private synchronized GraphQLRestServer getEndpoint() {
        if (this.endpoint != null) {
            return this.endpoint;
        }
        List graphqlProviders = this.pluginAccessor.getEnabledModulesByClass(GraphQLProviders.class);
        this.endpoint = GraphQLRestServer.builder().queryTypeName("Confluence").typeBuilderSupplier(JsonRootGraphQLTypeBuilder::new).providers(graphqlProviders).queryExecutionStrategy((ExecutionStrategy)new AsyncExecutionStrategyIgnoreUndefinedFields()).beforeRequest(this::beforeQuery).schemaBuildErrorHandler(this::handleSchemaError).errorConverter(this::convertError).queryExceptionHandler(this::queryExceptionHandler).build();
        return this.endpoint;
    }

    private void handleSchemaError(Object provider, Exception error) {
        log.error("Error building " + provider.getClass().getSimpleName() + ": " + error.getMessage(), (Throwable)error);
    }

    private Object convertError(GraphQLRestRequest request, Exception error) {
        if (error instanceof GraphQLRestException) {
            return ImmutableMap.of((Object)"statusCode", (Object)((GraphQLRestException)error).getStatusCode());
        }
        RestError restError = ExceptionConverter.Server.convertServiceException((Exception)error);
        if (restError.getStatusCode() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            log.error("Error returned from query for operation '" + ConfluenceGraphQLRestEndpoint.printOperationName(request) + "; '" + error.getMessage(), (Throwable)error);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue((Object)restError, Map.class);
    }

    private void queryExceptionHandler(GraphQLRestRequest request, Exception ex) {
        throw new RuntimeException("Error executing query for operation '" + ConfluenceGraphQLRestEndpoint.printOperationName(request) + "'; " + ex.getMessage(), ex);
    }

    private void beforeQuery(List<GraphQLRestRequest> requests) {
        for (GraphQLRestRequest request : requests) {
            request.setQueryDocument(this.parseAndCacheQuery(request));
        }
        new GraphQLQueryLimiter().checkQuery(requests);
    }

    private static String printOperationName(GraphQLRestRequest request) {
        return Strings.isNullOrEmpty((String)request.getOperationName()) ? request.getOperationName() : "<unnamed>";
    }

    private Document parseAndCacheQuery(GraphQLRestRequest request) {
        String query = !Strings.isNullOrEmpty((String)request.getQuery()) ? request.getQuery() : (request.getId() != null && request.getId().startsWith("sha256:") ? request.getId() : null);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput((String)query).operationName(request.getOperationName()).variables(request.getVariables()).build();
        PreparsedDocumentEntry result = this.cache.getDocument(executionInput, execInput -> {
            String queryString = execInput.getQuery();
            if (queryString.startsWith("sha256:")) {
                throw new SeeOtherException();
            }
            return new PreparsedDocumentEntry(PARSER.parseDocument(queryString));
        });
        if (result.getErrors() != null) {
            throw new BadRequestException("Error parsing query for operation '" + ConfluenceGraphQLRestEndpoint.printOperationName(request) + "'; " + ((GraphQLError)result.getErrors().get(0)).toString());
        }
        return result.getDocument();
    }
}

