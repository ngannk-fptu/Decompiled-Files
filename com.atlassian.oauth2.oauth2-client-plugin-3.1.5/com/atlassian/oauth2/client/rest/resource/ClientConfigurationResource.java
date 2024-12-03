/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequest
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.lib.flow.FlowResult
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.oauth2.client.RedirectUriSuffixGenerator;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import com.atlassian.oauth2.client.lib.web.AuthorizationCodeFlowUrlsProvider;
import com.atlassian.oauth2.client.rest.api.RestClientConfiguration;
import com.atlassian.oauth2.client.rest.api.RestFlowRequest;
import com.atlassian.oauth2.client.rest.api.RestFlowResult;
import com.atlassian.oauth2.client.rest.resource.validator.ClientConfigurationValidator;
import com.atlassian.oauth2.client.rest.resource.validator.ValidationException;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="config")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
public class ClientConfigurationResource {
    private static final Logger logger = LoggerFactory.getLogger(ClientConfigurationResource.class);
    private final ClientConfigStorageService clientConfigStorageService;
    private final FlowRequestService flowRequestService;
    private final ClientConfigurationValidator clientConfigurationValidator;
    private final PermissionEnforcer permissionEnforcer;
    private final AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider;
    private final RedirectUriSuffixGenerator redirectUriSuffixGenerator;
    private final I18nResolver i18nResolver;
    private final ClientHttpsValidator clientHttpsValidator;

    public ClientConfigurationResource(ClientConfigStorageService clientConfigStorageService, FlowRequestService flowRequestService, ClientConfigurationValidator clientConfigurationValidator, PermissionEnforcer permissionEnforcer, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider, RedirectUriSuffixGenerator redirectUriSuffixGenerator, I18nResolver i18nResolver, ClientHttpsValidator clientHttpsValidator) {
        this.clientConfigStorageService = clientConfigStorageService;
        this.flowRequestService = flowRequestService;
        this.clientConfigurationValidator = clientConfigurationValidator;
        this.permissionEnforcer = permissionEnforcer;
        this.authorizationCodeFlowUrlsProvider = authorizationCodeFlowUrlsProvider;
        this.redirectUriSuffixGenerator = redirectUriSuffixGenerator;
        this.i18nResolver = i18nResolver;
        this.clientHttpsValidator = clientHttpsValidator;
    }

    @GET
    @PublicApi
    public Response getAllClientConfigurations() throws ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Getting all client configurations");
        List results = this.clientConfigStorageService.list().stream().map(this::valueOf).collect(Collectors.toList());
        return Response.ok(results).build();
    }

    @POST
    @PublicApi
    public Response createClientConfiguration(RestClientConfiguration clientConfiguration) throws ConfigurationNotFoundException, ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Creating client configuration: [{}]", (Object)clientConfiguration);
        clientConfiguration.setId(null);
        ClientConfigurationEntity internalClientConfiguration = this.clientConfigurationValidator.validateCreate(clientConfiguration);
        ClientConfigurationEntity savedClientConfig = this.clientConfigStorageService.save(internalClientConfiguration);
        return Response.ok((Object)this.valueOf(savedClientConfig)).build();
    }

    @GET
    @Path(value="/{id}")
    @PublicApi
    public Response getClientConfiguration(@PathParam(value="id") String id) throws ConfigurationNotFoundException, ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Getting client configuration with id: [{}]", (Object)id);
        ClientConfigurationEntity configurationEntity = this.clientConfigStorageService.getByIdOrFail(id);
        return Response.ok((Object)this.valueOf(configurationEntity)).build();
    }

    @GET
    @Path(value="/name/{name}")
    @PublicApi
    public Response getClientConfigurationByName(@PathParam(value="name") String name) throws ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Getting client configuration with name: [{}]", (Object)name);
        return this.clientConfigStorageService.getByName(name).map(config -> Response.ok((Object)this.valueOf((ClientConfigurationEntity)config)).build()).orElseGet(() -> Response.status((Response.Status)Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path(value="/{id}")
    @PublicApi
    public Response deleteClientConfiguration(@PathParam(value="id") String id) throws ConfigurationNotFoundException, ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Deleting client configuration with id: [{}]", (Object)id);
        this.clientConfigStorageService.delete(id);
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path(value="/{id}")
    @PublicApi
    public Response updateClientConfiguration(@PathParam(value="id") String id, RestClientConfiguration newConfig) throws ConfigurationNotFoundException, ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Updating client configuration with id: [{}]", (Object)id);
        newConfig.setId(id);
        ClientConfigurationEntity existingConfig = this.clientConfigStorageService.getByIdOrFail(id);
        ClientConfigurationEntity validatedConfig = this.clientConfigurationValidator.validateUpdate(newConfig, existingConfig);
        ClientConfigurationEntity updatedConfig = ClientConfigurationEntity.builder((ClientConfigurationEntity)validatedConfig).id(id).build();
        this.clientConfigStorageService.save(updatedConfig);
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path(value="/get-redirect-uri")
    @PublicApi
    public Response generateRedirectUriSuffix(@QueryParam(value="authorizationEndpoint") String authorizationEndpoint) throws ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Generating redirect uri");
        String suffix = this.redirectUriSuffixGenerator.generateRedirectUriSuffix(authorizationEndpoint);
        String redirectUri = this.authorizationCodeFlowUrlsProvider.getRedirectUri(suffix).toString();
        ImmutableMap data = ImmutableMap.of((Object)"redirectUri", (Object)redirectUri, (Object)"redirectUriSuffix", (Object)suffix);
        return Response.ok((Object)data).build();
    }

    @GET
    @Path(value="/is-name-unique")
    @PublicApi
    public Response isNameUnique(@QueryParam(value="id") String id, @QueryParam(value="name") String name) throws ValidationException {
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Checking if name is unique");
        return Response.ok((Object)this.clientConfigStorageService.isNameUnique(id, name)).build();
    }

    @POST
    @Path(value="/flow")
    @PublicApi
    public Response createFlow(@Context HttpServletRequest request, @QueryParam(value="id") String id, @QueryParam(value="callbackUrl") String callbackUrl) throws ConfigurationNotFoundException, ValidationException {
        FlowRequest flowRequest;
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        logger.debug("Testing connection for configId {}", (Object)id);
        ClientConfigurationEntity clientConfigurationEntity = this.clientConfigStorageService.getByIdOrFail(id);
        try {
            flowRequest = this.flowRequestService.createFlowRequest(request.getSession(false), (ClientConfiguration)clientConfigurationEntity, configId -> callbackUrl + "/" + configId);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            throw new ValidationException(ErrorCollection.forMessage(this.i18nResolver.getText("oauth2.rest.error.flow.create.error")));
        }
        return Response.ok((Object)RestFlowRequest.valueOf(flowRequest)).build();
    }

    @GET
    @Path(value="/flow/{flowRequestId}")
    @PublicApi
    public Response getFlowResult(@Context HttpServletRequest request, @PathParam(value="flowRequestId") String flowRequestId) throws ValidationException {
        FlowResult flowRequest;
        this.permissionEnforcer.enforceSystemAdmin();
        this.enforceHttps();
        try {
            flowRequest = this.flowRequestService.getFlowResult(request.getSession(false), flowRequestId);
        }
        catch (IllegalArgumentException e) {
            throw new ValidationException(ErrorCollection.forMessage(this.i18nResolver.getText("oauth2.rest.error.flow.not.found", new Serializable[]{flowRequestId})));
        }
        if (!flowRequest.indicatesSuccess()) {
            String errorMessage = flowRequest.toErrorResult().getMessage();
            logger.error("Error occurred while authorizing an integration. The error message is: {}", (Object)errorMessage);
            return Response.ok((Object)new RestFlowResult(false, errorMessage)).build();
        }
        return Response.ok((Object)new RestFlowResult(true, "")).build();
    }

    private RestClientConfiguration valueOf(ClientConfigurationEntity clientConfiguration) {
        return RestClientConfiguration.valueOf(clientConfiguration, this.redirectUriSuffixGenerator.generateRedirectUriSuffix(clientConfiguration.getAuthorizationEndpoint()));
    }

    private void enforceHttps() throws ValidationException {
        if (this.clientHttpsValidator.isBaseUrlHttpsRequired() && !this.clientHttpsValidator.isBaseUrlHttps()) {
            throw new ValidationException(ErrorCollection.forMessage(this.i18nResolver.getText("oauth2.integrations.insecure.base.url.error.message")));
        }
    }
}

