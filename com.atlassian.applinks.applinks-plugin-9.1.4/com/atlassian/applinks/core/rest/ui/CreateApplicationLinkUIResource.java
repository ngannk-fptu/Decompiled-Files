/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.StaticUrlApplicationType
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.AuthenticationResponseException
 *  com.atlassian.applinks.spi.link.LinkCreationResponseException
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.NotAdministratorException
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.applinks.spi.link.RemoteErrorListException
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.analytics.ApplinksCreatedEventFactory;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.auth.OrphanedTrustDetector;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.CreateApplicationLinkRequestEntity;
import com.atlassian.applinks.core.rest.model.CreatedApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.ManifestEntity;
import com.atlassian.applinks.core.rest.model.OrphanedTrust;
import com.atlassian.applinks.core.rest.model.ResponseInfoEntity;
import com.atlassian.applinks.core.rest.model.VerifyTwoWayLinkDetailsRequestEntity;
import com.atlassian.applinks.core.rest.ui.AuthenticationResource;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.util.Holder;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.net.BasicHttpAuthRequestFactory;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.StaticUrlApplicationType;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.AuthenticationResponseException;
import com.atlassian.applinks.spi.link.LinkCreationResponseException;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.NotAdministratorException;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.link.RemoteErrorListException;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="applicationlinkForm")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class CreateApplicationLinkUIResource
extends AbstractResource {
    private static final boolean APPLINKS_ALLOW_ALL_HOSTS = Boolean.getBoolean("applinks.allow.all.hosts");
    protected final MutatingApplicationLinkService applicationLinkService;
    protected final ManifestRetriever manifestRetriever;
    protected final InternalHostApplication internalHostApplication;
    protected final I18nResolver i18nResolver;
    protected static final Logger LOG = LoggerFactory.getLogger(CreateApplicationLinkUIResource.class);
    protected final OrphanedTrustDetector orphanedTrustDetector;
    protected final PluginAccessor pluginAccessor;
    protected final UserManager userManager;
    private final ApplinksCreatedEventFactory applinksCreatedEventFactory;
    private final EventPublisher eventPublisher;

    public CreateApplicationLinkUIResource(MutatingApplicationLinkService applicationLinkService, RequestFactory requestFactory, InternalHostApplication internalHostApplication, I18nResolver i18nResolver, InternalTypeAccessor typeAccessor, ManifestRetriever manifestRetriever, RestUrlBuilder restUrlBuilder, @Qualifier(value="delegatingOrphanedTrustDetector") OrphanedTrustDetector orphanedTrustDetector, PluginAccessor pluginAccessor, UserManager userManager, ApplinksCreatedEventFactory applinksCreatedEventFactory, EventPublisher eventPublisher) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.i18nResolver = i18nResolver;
        this.internalHostApplication = internalHostApplication;
        this.applicationLinkService = applicationLinkService;
        this.manifestRetriever = manifestRetriever;
        this.orphanedTrustDetector = orphanedTrustDetector;
        this.pluginAccessor = pluginAccessor;
        this.userManager = userManager;
        this.applinksCreatedEventFactory = applinksCreatedEventFactory;
        this.eventPublisher = eventPublisher;
    }

    private void sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON reason) {
        this.eventPublisher.publish((Object)this.applinksCreatedEventFactory.createFailEvent(reason));
    }

    private void sendWarningEvent(ApplinksCreatedEventFactory.FAILURE_REASON reason) {
        this.eventPublisher.publish((Object)this.applinksCreatedEventFactory.createWarningEvent(reason));
    }

    private void sendSuccessEvent() {
        this.eventPublisher.publish((Object)this.applinksCreatedEventFactory.createSuccessEvent());
    }

    @GET
    @Path(value="manifest")
    @RequiresXsrfCheck
    public Response tryToFetchManifest(@QueryParam(value="url") String url) {
        Manifest manifest;
        block17: {
            if (StringUtils.isBlank((CharSequence)url)) {
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.rpcurl"));
            }
            if (!APPLINKS_ALLOW_ALL_HOSTS) {
                RequestUtil.validateUriAgainstBlocklist(url, this.i18nResolver);
            }
            try {
                LOG.debug("URL received '" + url + "'");
                URI manifestUrl = new URL(url).toURI();
                manifest = this.manifestRetriever.getManifest(manifestUrl);
            }
            catch (ManifestNotFoundException e) {
                LOG.error("ManifestNotFoundException thrown while retrieving manifest", (Throwable)e);
                manifest = null;
                Throwable responseException = e.getCause();
                if (responseException != null) {
                    if (responseException instanceof AppLinksManifestDownloader.ManifestGotRedirectedException) {
                        AppLinksManifestDownloader.ManifestGotRedirectedException mgre = (AppLinksManifestDownloader.ManifestGotRedirectedException)responseException;
                        Map<String, String> redirectedUrl = Collections.singletonMap("redirectedUrl", mgre.getNewLocation());
                        this.sendWarningEvent(ApplinksCreatedEventFactory.FAILURE_REASON.REDIRECT);
                        return RestUtil.ok(new ResponseInfoEntity("applinks.warning.redirected.host", this.getRedirectionWarning(mgre), redirectedUrl));
                    }
                    if (responseException instanceof IOException) {
                        this.sendWarningEvent(ApplinksCreatedEventFactory.FAILURE_REASON.NO_RESPONSE);
                        return RestUtil.ok(new ResponseInfoEntity("applinks.warning.unknown.host", this.getNonResponsiveHostWarning()));
                    }
                }
            }
            catch (Exception e) {
                LOG.error("Exception thrown while retrieving manifest", (Throwable)e);
                Pattern p = Pattern.compile("http(s)?:/[^/].*");
                Matcher m = p.matcher(url);
                if (m.matches()) {
                    LOG.warn("The url '" + url + "' is missing the double slashes after the protocol. Is there a proxy server in the middle that has replaced the '//' with a single '/'?");
                    this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.NO_DOUBLE_SLASHES);
                } else {
                    this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.INVALID_URL);
                }
                LOG.debug("Invalid URL url='" + url + "'", (Throwable)e);
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.url.invalid", new Serializable[]{url}));
            }
            if (manifest != null) {
                LOG.debug("Manifest retrieved successfully");
                try {
                    if (this.typeAccessor.loadApplicationType(manifest.getTypeId()) == null) {
                        throw new TypeNotInstalledException(manifest.getTypeId().get(), manifest.getName(), manifest.getUrl());
                    }
                    MutableApplicationLink existingAppLink = this.applicationLinkService.getApplicationLink(manifest.getId());
                    if (existingAppLink != null) {
                        if (existingAppLink.getDisplayUrl().equals(manifest.getUrl())) {
                            this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.ALREADY_CONFIGURED);
                            return RestUtil.conflict(this.i18nResolver.getText("applinks.error.applink.exists", new Serializable[]{manifest.getUrl()}));
                        }
                        this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.ALREADY_CONFIGURED_UNDER_DIFFERENT_URL);
                        return RestUtil.conflict(this.i18nResolver.getText("applinks.error.applink.exists.with.different.url"));
                    }
                    break block17;
                }
                catch (TypeNotInstalledException e) {
                    LOG.error("TypeNotInstalledException thrown", (Throwable)e);
                    this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.TYPE_NOT_INSTALLED);
                    return RestUtil.badRequest(String.format(this.i18nResolver.getText("applinks.error.remote.type.not.installed", new Serializable[]{e.getType()}), new Object[0]));
                }
            }
            LOG.error("Null manifest retrieved");
            this.sendWarningEvent(ApplinksCreatedEventFactory.FAILURE_REASON.NULL_MANIFEST);
            return RestUtil.ok(new ResponseInfoEntity());
        }
        if (manifest.getId().equals((Object)this.internalHostApplication.getId())) {
            this.sendFailureEvent(ApplinksCreatedEventFactory.FAILURE_REASON.LINK_TO_SELF);
            return RestUtil.conflict(this.i18nResolver.getText("applinks.error.applink.itsme"));
        }
        this.sendSuccessEvent();
        return RestUtil.ok(new ManifestEntity(manifest));
    }

    private String getRedirectionWarning(AppLinksManifestDownloader.ManifestGotRedirectedException mgre) {
        return this.i18nResolver.getText("applinks.warning.redirected.host.new", new Serializable[]{StringEscapeUtils.escapeHtml4((String)mgre.newLocationBaseUrl())});
    }

    private String getNonResponsiveHostWarning() {
        return this.i18nResolver.getText("applinks.warning.unknown.host.new");
    }

    @POST
    @Path(value="createStaticUrlAppLink")
    public Response createStaticUrlAppLink(@QueryParam(value="typeId") String typeId) throws Exception {
        StaticUrlApplicationType type = (StaticUrlApplicationType)this.typeAccessor.loadApplicationType(typeId);
        Manifest manifest = this.manifestRetriever.getManifest(type.getStaticUrl(), (ApplicationType)type);
        ApplicationLinkDetails details = ApplicationLinkDetails.builder().name(type.getI18nKey()).displayUrl(type.getStaticUrl()).rpcUrl(type.getStaticUrl()).isPrimary(true).build();
        MutableApplicationLink createdApplicationLink = this.applicationLinkService.addApplicationLink(manifest.getId(), (ApplicationType)type, details);
        return RestUtil.ok(new CreatedApplicationLinkEntity(this.toApplicationLinkEntity((ApplicationLink)createdApplicationLink), true));
    }

    @POST
    @Path(value="createAppLink")
    public Response createApplicationLink(CreateApplicationLinkRequestEntity applicationLinkRequest) {
        ApplicationLink createdApplicationLink;
        ApplicationLinkEntity applicationLink = applicationLinkRequest.getApplicationLink();
        URI remoteRpcUrl = applicationLink.getRpcUrl();
        if (StringUtils.isEmpty((CharSequence)applicationLink.getName().trim())) {
            return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.appname")}), Lists.newArrayList((Object[])new String[]{"application-name"}));
        }
        if (StringUtils.isEmpty((CharSequence)applicationLink.getTypeId().get()) || this.typeAccessor.loadApplicationType(applicationLink.getTypeId()) == null) {
            return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.apptype")}), Lists.newArrayList((Object[])new String[]{"application-types"}));
        }
        final boolean shareUserbase = applicationLinkRequest.getConfigFormValues().shareUserbase();
        final boolean trustEachOther = applicationLinkRequest.getConfigFormValues().trustEachOther();
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUsername()) && shareUserbase) {
            return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.only.sysadmin.operation")}), Lists.newArrayList((Object[])new String[]{"same-userbase"}));
        }
        if (this.applicationLinkWithRpcUrlAlreadyExists(applicationLink.getRpcUrl())) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.rpcurl.exists"));
        }
        if (applicationLinkRequest.createTwoWayLink()) {
            try {
                this.applicationLinkService.createReciprocalLink(remoteRpcUrl, applicationLinkRequest.isCustomRpcURL() ? applicationLinkRequest.getRpcUrl() : null, applicationLinkRequest.getUsername(), applicationLinkRequest.getPassword());
            }
            catch (NotAdministratorException exception) {
                return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.unauthorized")}), Lists.newArrayList((Object[])new String[]{"authorization"}));
            }
            catch (LinkCreationResponseException exception) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.error.response"));
            }
            catch (AuthenticationResponseException exception) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.error.authorization.response"));
            }
            catch (RemoteErrorListException exception) {
                ArrayList errors = Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.general")});
                errors.addAll(exception.getErrors());
                return RestUtil.badRequest(errors.toArray(new String[0]));
            }
            catch (ReciprocalActionException exception) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.error.general"));
            }
        }
        ApplicationType type = this.typeAccessor.loadApplicationType(applicationLink.getTypeId().get());
        try {
            createdApplicationLink = this.applicationLinkService.createApplicationLink(type, applicationLink.getDetails());
        }
        catch (ManifestNotFoundException e) {
            return RestUtil.serverError(this.i18nResolver.getText("applinks.error.incorrect.application.type"));
        }
        boolean autoConfigurationSuccessful = true;
        if (applicationLinkRequest.createTwoWayLink()) {
            try {
                this.applicationLinkService.configureAuthenticationForApplicationLink(createdApplicationLink, new AuthenticationScenario(){

                    public boolean isCommonUserBase() {
                        return shareUserbase;
                    }

                    public boolean isTrusted() {
                        return trustEachOther;
                    }
                }, applicationLinkRequest.getUsername(), applicationLinkRequest.getPassword());
            }
            catch (AuthenticationConfigurationException e) {
                LOG.warn("Error during auto-configuration of authentication providers for application link '" + createdApplicationLink + "'", (Throwable)e);
                autoConfigurationSuccessful = false;
            }
        }
        if (applicationLinkRequest.getOrphanedTrust() != null) {
            OrphanedTrust orphanedTrust = applicationLinkRequest.getOrphanedTrust();
            try {
                OrphanedTrustCertificate.Type certificateType = OrphanedTrustCertificate.Type.valueOf(orphanedTrust.getType());
                this.orphanedTrustDetector.addOrphanedTrustToApplicationLink(orphanedTrust.getId(), certificateType, createdApplicationLink.getId());
                if (applicationLinkRequest.createTwoWayLink()) {
                    AutoConfiguringAuthenticatorProviderPluginModule providerPluginModule = this.getAutoConfigurationPluginModule(certificateType);
                    if (providerPluginModule != null) {
                        providerPluginModule.enable(this.getAuthenticatedRequestFactory(applicationLinkRequest), createdApplicationLink);
                    } else {
                        LOG.warn("Failed to find an authentication type for the orphaned trust certificate type='" + orphanedTrust.getType() + "' and id='" + orphanedTrust.getId() + "' that supports auto-configuration");
                    }
                }
            }
            catch (Exception e) {
                LOG.error("Failed to add orphaned trust certificate with type='" + orphanedTrust.getType() + "' and id='" + orphanedTrust.getId() + "'", (Throwable)e);
            }
        }
        return RestUtil.ok(new CreatedApplicationLinkEntity(this.toApplicationLinkEntity(createdApplicationLink), autoConfigurationSuccessful));
    }

    private boolean applicationLinkWithRpcUrlAlreadyExists(URI rpcURl) {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).anyMatch(input -> input.getRpcUrl().equals(rpcURl));
    }

    private AutoConfiguringAuthenticatorProviderPluginModule getAutoConfigurationPluginModule(OrphanedTrustCertificate.Type certificateType) {
        return this.findAutoConfiguringAuthenticationProviderModule(certificateType);
    }

    private AutoConfiguringAuthenticatorProviderPluginModule findAutoConfiguringAuthenticationProviderModule(OrphanedTrustCertificate.Type certificateType) {
        List authenticationProviderModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(AuthenticationProviderModuleDescriptor.class);
        for (AuthenticationProviderModuleDescriptor authenticationProviderModuleDescriptor : authenticationProviderModuleDescriptors) {
            if (!(authenticationProviderModuleDescriptor instanceof OrphanedTrustAwareAuthenticatorProviderPluginModule)) continue;
            if (((OrphanedTrustAwareAuthenticatorProviderPluginModule)((Object)authenticationProviderModuleDescriptor)).isApplicable(certificateType.name())) {
                // empty if block
            }
            return (AutoConfiguringAuthenticatorProviderPluginModule)authenticationProviderModuleDescriptor.getModule();
        }
        return null;
    }

    private BasicHttpAuthRequestFactory<Request<Request<?, com.atlassian.sal.api.net.Response>, com.atlassian.sal.api.net.Response>> getAuthenticatedRequestFactory(CreateApplicationLinkRequestEntity applicationLinkRequest) {
        return new BasicHttpAuthRequestFactory(this.requestFactory, applicationLinkRequest.getUsername(), applicationLinkRequest.getPassword());
    }

    @POST
    @Path(value="details")
    public Response verifyTwoWayLinkDetails(VerifyTwoWayLinkDetailsRequestEntity linkDetails) throws TypeNotInstalledException {
        boolean isAdminUser;
        try {
            isAdminUser = this.applicationLinkService.isAdminUserInRemoteApplication(linkDetails.getRemoteUrl(), linkDetails.getUsername(), linkDetails.getPassword());
        }
        catch (ResponseException e) {
            LOG.error("Error occurred while checking credentials.", (Throwable)e);
            return RestUtil.serverError(this.i18nResolver.getText("applinks.error.authorization.response"));
        }
        if (isAdminUser) {
            String applicationType = this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey());
            try {
                if (this.isRpcUrlValid(linkDetails.getRemoteUrl(), linkDetails.getRpcUrl(), linkDetails.getUsername(), linkDetails.getPassword())) {
                    return RestUtil.ok();
                }
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.url.reciprocal.rpc.url.invalid", new Serializable[]{this.internalHostApplication.getName(), applicationType, linkDetails.getRpcUrl()}));
            }
            catch (ResponseException e) {
                LOG.error("Error occurred while checking reciprocal link.", (Throwable)e);
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.url.reciprocal.rpc.url.invalid", new Serializable[]{this.internalHostApplication.getName(), applicationType, linkDetails.getRpcUrl()}));
            }
        }
        return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.unauthorized")}), Lists.newArrayList((Object[])new String[]{"reciprocal-link-password"}));
    }

    private boolean isRpcUrlValid(URI url, URI rpcUrl, String username, String password) throws ResponseException {
        String pathUrl = this.getUrlFor(URIUtil.uncheckedConcatenate(url, "/rest/applinks/1.0/"), AuthenticationResource.class).rpcUrlIsReachable(this.internalHostApplication.getId().get(), rpcUrl, null).toString();
        String urlWithQuery = pathUrl + "?url=" + URIUtil.utf8Encode(rpcUrl);
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, urlWithQuery);
        request.addBasicAuthentication(url.getHost(), username, password);
        final Holder<Boolean> rpcUrlValid = new Holder<Boolean>(false);
        request.execute((ResponseHandler)new ResponseHandler<com.atlassian.sal.api.net.Response>(){

            public void handle(com.atlassian.sal.api.net.Response restResponse) throws ResponseException {
                if (restResponse.isSuccessful()) {
                    rpcUrlValid.set(true);
                }
            }
        });
        return rpcUrlValid.get();
    }
}

