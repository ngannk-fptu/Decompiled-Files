/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.ui.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.ui.AbstractAppLinksAdminOnlyServlet;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatorContainerServlet
extends AbstractAppLinksAdminOnlyServlet {
    private static final String APPLICATION_ID = "applicationId";
    private static final String DIRECTION = "direction";
    private static final Set<String> TWO_LO_MODULE_KEYS = ImmutableSet.of((Object)"TwoLeggedOAuthAuthenticatorProviderPluginModule", (Object)"TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule");
    private static final String SUCCESS_TEMPLATE = "com/atlassian/applinks/ui/auth_container.vm";
    private final ApplicationLinkService applicationLinkService;
    private final ManifestRetriever manifestRetriever;
    private final PluginAccessor pluginAccessor;
    private final WebSudoManager webSudoManager;
    private final UserManager userManager;
    private static final Predicate<Class<? extends AuthenticationProvider>> IS_OAUTH = new Predicate<Class<? extends AuthenticationProvider>>(){

        public boolean apply(Class<? extends AuthenticationProvider> providerClazz) {
            return OAuthAuthenticationProvider.class.isAssignableFrom(providerClazz);
        }
    };

    public AuthenticatorContainerServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, InternalHostApplication internalHostApplication, ManifestRetriever manifestRetriever, PluginAccessor pluginAccessor, AdminUIAuthenticator adminUIAuthenticator, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.applicationLinkService = applicationLinkService;
        this.manifestRetriever = manifestRetriever;
        this.pluginAccessor = pluginAccessor;
        this.webSudoManager = webSudoManager;
        this.userManager = userManager;
    }

    @Override
    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-plugin:auth-container");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ApplicationLink link;
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationId applicationId = new ApplicationId(this.getRequiredParameter(request, APPLICATION_ID));
            try {
                link = this.applicationLinkService.getApplicationLink(applicationId);
            }
            catch (TypeNotInstalledException e) {
                throw new IllegalStateException(String.format("Failed to load application %s as the %s type is not installed", applicationId, e.getType()));
            }
            if (link == null) {
                this.logger.error("Couldn't find application link with id " + applicationId);
                throw new AbstractApplinksServlet.NotFoundException(this.messageFactory.newI18nMessage("auth.oauth.config.error.link.id", new Serializable[]{applicationId.toString()}));
            }
            AuthenticationDirection direction = AuthenticationDirection.valueOf((String)this.getRequiredParameter(request, DIRECTION));
            try {
                Manifest manifest = this.manifestRetriever.getManifest(link.getRpcUrl(), link.getType());
                Object authenticationProviderClasses = direction == AuthenticationDirection.INBOUND ? Sets.intersection((Set)Sets.newHashSet((Iterable)this.internalHostApplication.getSupportedInboundAuthenticationTypes()), (Set)manifest.getOutboundAuthenticationTypes()) : Sets.intersection((Set)Sets.newHashSet((Iterable)this.internalHostApplication.getSupportedOutboundAuthenticationTypes()), (Set)manifest.getInboundAuthenticationTypes());
                ArrayList<ConfigTab> tabs = new ArrayList<ConfigTab>();
                ArrayList descriptors = Lists.newArrayList((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(AuthenticationProviderModuleDescriptor.class));
                Collections.sort(descriptors, AuthenticationProviderModuleDescriptor.BY_WEIGHT);
                if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
                    authenticationProviderClasses = Sets.filter((Set)authenticationProviderClasses, IS_OAUTH);
                }
                HashSet<Class> includedProviderTypes = new HashSet<Class>();
                for (AuthenticationProviderModuleDescriptor descriptor : descriptors) {
                    AuthenticationProviderPluginModule module = descriptor.getModule();
                    if (!authenticationProviderClasses.contains(module.getAuthenticationProviderClass())) continue;
                    String providerType = module.getAuthenticationProviderClass().getName();
                    String cssClass = providerType.substring(providerType.lastIndexOf(".") + 1);
                    String configUrl = module.getConfigUrl(link, manifest.getAppLinksVersion(), direction, request);
                    if (configUrl == null || TWO_LO_MODULE_KEYS.contains(descriptor.getKey())) continue;
                    tabs.add(new ConfigTab(this.messageFactory.newI18nMessage(descriptor.getI18nNameKey(), new Serializable[0]), configUrl, descriptor.getKey(), cssClass));
                    includedProviderTypes.add(module.getAuthenticationProviderClass());
                }
                RendererContextBuilder contextBuilder = this.createContextBuilder(link);
                contextBuilder.put("tabs", tabs).put(DIRECTION, direction.name());
                this.render(SUCCESS_TEMPLATE, contextBuilder.build(), request, response);
            }
            catch (ManifestNotFoundException e) {
                this.logger.error("Failed to retrieve manifest for application link '" + link + "'.");
                throw new AbstractApplinksServlet.NotFoundException(this.messageFactory.newI18nMessage("auth.config.manifest.missing", new Serializable[]{link.getName()}));
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    public static class ConfigTab {
        private final Message name;
        private final String url;
        private final String id;
        private final String cssClass;

        public ConfigTab(Message name, String url, String id, String cssClass) {
            this.name = name;
            this.url = url;
            this.id = id;
            this.cssClass = cssClass;
        }

        public Message getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        public String getId() {
            return this.id;
        }

        public String getCssClass() {
            return this.cssClass;
        }
    }
}

