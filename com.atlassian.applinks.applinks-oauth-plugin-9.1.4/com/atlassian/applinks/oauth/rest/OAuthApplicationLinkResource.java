/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.core.InternalTypeAccessor
 *  com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor
 *  com.atlassian.applinks.core.rest.context.ContextInterceptor
 *  com.atlassian.applinks.core.rest.model.ApplicationLinkAuthenticationEntity
 *  com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity
 *  com.atlassian.applinks.core.rest.model.ConsumerEntity
 *  com.atlassian.applinks.core.rest.model.ConsumerEntityListEntity
 *  com.atlassian.applinks.core.rest.util.RestUtil
 *  com.atlassian.applinks.core.v1.rest.ApplicationLinkResource
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$InstanceBuilder
 *  com.atlassian.oauth.Consumer$SignatureMethod
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.oauth.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkAuthenticationEntity;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import com.atlassian.applinks.core.rest.model.ConsumerEntity;
import com.atlassian.applinks.core.rest.model.ConsumerEntityListEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.v1.rest.ApplicationLinkResource;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.rest.ConsumerEntityBuilder;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="applicationlink")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@WebSudoRequired
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class OAuthApplicationLinkResource
extends ApplicationLinkResource {
    private final PluginAccessor pluginAccessor;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ConsumerService consumerService;

    public OAuthApplicationLinkResource(MutatingApplicationLinkService applicationLinkService, I18nResolver i18nResolver, InternalTypeAccessor typeAccessor, ManifestRetriever manifestRetriever, RestUrlBuilder restUrlBuilder, RequestFactory requestFactory, UserManager userManager, PluginAccessor pluginAccessor, AuthenticationConfigurationManager authenticationConfigurationManager, ServiceProviderStoreService serviceProviderStoreService, ConsumerService consumerService) {
        super(applicationLinkService, i18nResolver, typeAccessor, manifestRetriever, restUrlBuilder, requestFactory, userManager);
        this.pluginAccessor = pluginAccessor;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.consumerService = consumerService;
    }

    @GET
    @Path(value="{id}/authentication")
    public Response getAuthentication(@PathParam(value="id") String id) throws TypeNotInstalledException, URISyntaxException {
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound((String)this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        List<AuthenticationProviderEntity> configuredAuthProviders = this.getConfiguredProviders(applicationLink);
        Iterable<Consumer> consumers = this.findConsumers(applicationLink, configuredAuthProviders);
        ArrayList consumerEntities = Lists.newArrayList();
        for (Consumer consumer : consumers) {
            consumerEntities.add(ConsumerEntityBuilder.consumer(consumer).self(new URI("applicationlink/" + id + "/authentication/consumer")).build());
        }
        return RestUtil.ok((Object)new ApplicationLinkAuthenticationEntity(Link.self((URI)new URI("applicationlink/" + id + "/authentication")), (List)consumerEntities, configuredAuthProviders));
    }

    @GET
    @Path(value="{id}/authentication/consumer")
    public Response getConsumer(@PathParam(value="id") String id) throws TypeNotInstalledException, URISyntaxException {
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound((String)this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        List<AuthenticationProviderEntity> configuredAuthProviders = this.getConfiguredProviders(applicationLink);
        Iterable<Consumer> consumers = this.findConsumers(applicationLink, configuredAuthProviders);
        if (!consumers.iterator().hasNext() && applicationLink.getType() instanceof GenericApplicationType && configuredAuthProviders.size() == 0) {
            return RestUtil.notFound((String)this.i18nResolver.getText("applinks.generic.consumer.needs.authenticationprovider", new Serializable[]{id}));
        }
        if (!consumers.iterator().hasNext()) {
            return RestUtil.notFound((String)this.i18nResolver.getText("applinks.consumer.notfound", new Serializable[]{id}));
        }
        ArrayList consumerEntities = Lists.newArrayList();
        for (Consumer consumer : consumers) {
            consumerEntities.add(ConsumerEntityBuilder.consumer(consumer).self(new URI("applicationlink/" + id + "/authentication/consumer")).build());
        }
        return RestUtil.ok((Object)new ConsumerEntityListEntity((List)consumerEntities));
    }

    @PUT
    @Path(value="{id}/authentication/consumer")
    public Response putConsumer(@PathParam(value="id") String id, @QueryParam(value="autoConfigure") Boolean autoConfigure, ConsumerEntity consumerEntity) throws TypeNotInstalledException, URISyntaxException {
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound((String)this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        if (autoConfigure != null && autoConfigure.booleanValue()) {
            try {
                Consumer consumer = OAuthHelper.fetchConsumerInformation(applicationLink);
                Consumer updatedConsumer = new Consumer.InstanceBuilder(consumer.getKey()).name(consumer.getName()).description(consumer.getDescription()).publicKey(consumer.getPublicKey()).signatureMethod(consumer.getSignatureMethod()).callback(consumer.getCallback()).twoLOAllowed(consumerEntity.isTwoLOAllowed()).executingTwoLOUser(consumerEntity.getExecutingTwoLOUser()).twoLOImpersonationAllowed(consumerEntity.isTwoLOImpersonationAllowed()).build();
                this.serviceProviderStoreService.addConsumer(updatedConsumer, applicationLink);
            }
            catch (ResponseException e) {
                return RestUtil.serverError((String)this.i18nResolver.getText("applinks.consumer.autoconfigure.consumerInfo.notfound"));
            }
            return RestUtil.created((Link)Link.self((URI)new URI("applicationlink/" + id + "/authentication/consumer")));
        }
        if (applicationLink.getType() instanceof GenericApplicationType) {
            List<String> errors = this.validate3rdPartyConsumer(consumerEntity);
            if (errors.size() > 0) {
                return RestUtil.badRequest((String[])errors.toArray(new String[errors.size()]));
            }
            if (consumerEntity.isOutgoing()) {
                this.add3rdPartyOutgoingConsumer(consumerEntity);
            } else {
                try {
                    Consumer consumer = this.createBasicConsumer(consumerEntity, applicationLink);
                    this.serviceProviderStoreService.addConsumer(consumer, applicationLink);
                }
                catch (NoSuchAlgorithmException e) {
                    return RestUtil.badRequest((String[])new String[]{this.i18nResolver.getText("applinks.invalid.consumer.publickey", new Serializable[]{id})});
                }
                catch (InvalidKeySpecException e) {
                    return RestUtil.badRequest((String[])new String[]{this.i18nResolver.getText("applinks.invalid.consumer.publickey", new Serializable[]{id})});
                }
            }
        } else {
            List<String> errors = this.validateAtlassianConsumer(consumerEntity);
            if (errors.size() > 0) {
                return RestUtil.badRequest((String[])errors.toArray(new String[errors.size()]));
            }
            try {
                Consumer consumer = this.createBasicConsumer(consumerEntity, applicationLink);
                Consumer updatedConsumer = new Consumer.InstanceBuilder(consumer.getKey()).name(consumer.getName()).description(consumer.getDescription()).publicKey(consumer.getPublicKey()).signatureMethod(consumer.getSignatureMethod()).callback(consumer.getCallback()).twoLOAllowed(consumerEntity.isTwoLOAllowed()).executingTwoLOUser(consumerEntity.getExecutingTwoLOUser()).twoLOImpersonationAllowed(consumerEntity.isTwoLOImpersonationAllowed()).build();
                this.serviceProviderStoreService.addConsumer(updatedConsumer, applicationLink);
            }
            catch (NoSuchAlgorithmException e) {
                return RestUtil.badRequest((String[])new String[]{this.i18nResolver.getText("applinks.invalid.consumer.publickey", new Serializable[]{id})});
            }
            catch (InvalidKeySpecException e) {
                return RestUtil.badRequest((String[])new String[]{this.i18nResolver.getText("applinks.invalid.consumer.publickey", new Serializable[]{id})});
            }
        }
        return RestUtil.created((Link)Link.self((URI)new URI("applicationlink/" + id + "/authentication/consumer")));
    }

    private Consumer add3rdPartyOutgoingConsumer(ConsumerEntity consumerEntity) {
        Consumer consumer = Consumer.key((String)consumerEntity.getKey()).name(consumerEntity.getName()).signatureMethod(Consumer.SignatureMethod.HMAC_SHA1).description(consumerEntity.getDescription()).build();
        this.consumerService.add(consumer.getName(), consumer, consumerEntity.getSharedSecret());
        return consumer;
    }

    private Consumer createBasicConsumer(ConsumerEntity consumerEntity, ApplicationLink applicationLink) throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        return Consumer.key((String)consumerEntity.getKey()).name(consumerEntity.getName()).publicKey(RSAKeys.fromPemEncodingToPublicKey((String)consumerEntity.getPublicKey())).description(consumerEntity.getDescription()).callback(consumerEntity.getCallback()).build();
    }

    private List<String> validate3rdPartyConsumer(ConsumerEntity consumerEntity) {
        ArrayList errors = Lists.newArrayList();
        if (StringUtils.isEmpty((CharSequence)consumerEntity.getKey())) {
            errors.add(this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.key.is.required"));
        }
        if (consumerEntity.isOutgoing()) {
            if (StringUtils.isEmpty((CharSequence)consumerEntity.getName())) {
                errors.add(this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.name.is.required"));
            }
            if (StringUtils.isEmpty((CharSequence)consumerEntity.getSharedSecret())) {
                errors.add(this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.shared.secret.is.required"));
            }
        } else if (StringUtils.isEmpty((CharSequence)consumerEntity.getPublicKey())) {
            errors.add(this.i18nResolver.getText("auth.oauth.config.serviceprovider.missing.public.key"));
        }
        return errors;
    }

    private List<String> validateAtlassianConsumer(ConsumerEntity consumerEntity) {
        ArrayList errors = Lists.newArrayList();
        if (StringUtils.isEmpty((CharSequence)consumerEntity.getKey())) {
            errors.add(this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.key.is.required"));
        }
        if (StringUtils.isEmpty((CharSequence)consumerEntity.getName())) {
            errors.add(this.i18nResolver.getText("auth.oauth.config.consumer.serviceprovider.name.is.required"));
        }
        if (StringUtils.isEmpty((CharSequence)consumerEntity.getPublicKey())) {
            errors.add(this.i18nResolver.getText("applinks.consumer.publickey.required"));
        }
        return errors;
    }

    private List<AuthenticationProviderEntity> getConfiguredProviders(ApplicationLink applicationLink) throws URISyntaxException {
        return this.getConfiguredProviders(applicationLink, this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class));
    }

    private List<AuthenticationProviderEntity> getConfiguredProviders(ApplicationLink applicationLink, Iterable<AuthenticationProviderPluginModule> pluginModules) throws URISyntaxException {
        ArrayList<AuthenticationProviderEntity> configuredAuthProviders = new ArrayList<AuthenticationProviderEntity>();
        for (AuthenticationProviderPluginModule authenticationProviderPluginModule : pluginModules) {
            AuthenticationProvider authenticationProvider = authenticationProviderPluginModule.getAuthenticationProvider(applicationLink);
            if (authenticationProvider == null) continue;
            Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), authenticationProviderPluginModule.getAuthenticationProviderClass());
            configuredAuthProviders.add(new AuthenticationProviderEntity(Link.self((URI)new URI("applicationlink/" + applicationLink.getId().toString() + "/authentication/provider")), authenticationProviderPluginModule.getClass().getName(), authenticationProviderPluginModule.getAuthenticationProviderClass().getName(), config));
        }
        return configuredAuthProviders;
    }

    private ApplicationLink findApplicationLink(String id) throws TypeNotInstalledException {
        ApplicationId applicationId;
        try {
            applicationId = new ApplicationId(id);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        return this.applicationLinkService.getApplicationLink(applicationId);
    }

    private Iterable<Consumer> findConsumers(ApplicationLink applicationLink, List<AuthenticationProviderEntity> configuredAuthProviders) {
        ArrayList consumers = Lists.newArrayList();
        Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
        if (consumer != null) {
            consumers.add(consumer);
        }
        if (applicationLink.getType() instanceof GenericApplicationType) {
            for (AuthenticationProviderEntity entity : configuredAuthProviders) {
                Consumer genericOutGoingConsumer;
                String consumerKey;
                if (!(applicationLink.getType() instanceof GenericApplicationType) || StringUtils.isEmpty((CharSequence)(consumerKey = (String)entity.getConfig().get("consumerKey.outbound"))) || (genericOutGoingConsumer = this.consumerService.getConsumerByKey(consumerKey)) == null) continue;
                consumers.add(genericOutGoingConsumer);
            }
        }
        return consumers;
    }
}

