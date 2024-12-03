/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.sun.jersey.api.core.HttpContext
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.AuthenticatorAccessor;
import com.atlassian.applinks.core.concurrent.ConcurrentExecutor;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.rest.model.LinkAndAuthProviderEntity;
import com.atlassian.applinks.core.rest.model.ListEntity;
import com.atlassian.applinks.core.rest.model.WebItemEntityList;
import com.atlassian.applinks.core.rest.model.WebPanelEntityList;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.webfragment.WebFragmentContext;
import com.atlassian.applinks.core.webfragment.WebFragmentHelper;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="listApplicationlinks")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ListApplicationLinksUIResource
extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(ListApplicationLinksUIResource.class);
    private final MutatingApplicationLinkService applicationLinkService;
    private final InternalHostApplication internalHostApplication;
    private final ManifestRetriever manifestRetriever;
    private final I18nResolver i18nResolver;
    private final WebFragmentHelper webFragmentHelper;
    private final AuthenticatorAccessor authenticatorAccessor;
    private final ConcurrentExecutor executor;

    public ListApplicationLinksUIResource(MutatingApplicationLinkService applicationLinkService, InternalHostApplication internalHostApplication, ManifestRetriever manifestRetriever, I18nResolver i18nResolver, WebFragmentHelper webFragmentHelper, RestUrlBuilder restUrlBuilder, AuthenticatorAccessor authenticatorAccessor, RequestFactory requestFactory, InternalTypeAccessor typeAccessor, ConcurrentExecutor executor) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.applicationLinkService = applicationLinkService;
        this.internalHostApplication = internalHostApplication;
        this.manifestRetriever = manifestRetriever;
        this.i18nResolver = i18nResolver;
        this.webFragmentHelper = webFragmentHelper;
        this.authenticatorAccessor = authenticatorAccessor;
        this.executor = executor;
    }

    protected LinkAndAuthProviderEntity getLinkAndAuthProviderEntity(ApplicationLink applicationLink) {
        LinkedHashSet<Class<? extends AuthenticationProvider>> configuredOutgoingAuthenticationProviders = new LinkedHashSet<Class<? extends AuthenticationProvider>>();
        LinkedHashSet<Class<? extends AuthenticationProvider>> configuredIncomingAuthenticationProviders = new LinkedHashSet<Class<? extends AuthenticationProvider>>();
        for (AuthenticationProviderPluginModule authenticationProviderPluginModule : this.authenticatorAccessor.getAllAuthenticationProviderPluginModules()) {
            AuthenticationProvider authenticationProvider = authenticationProviderPluginModule.getAuthenticationProvider(applicationLink);
            if (authenticationProvider != null) {
                configuredOutgoingAuthenticationProviders.add(authenticationProviderPluginModule.getAuthenticationProviderClass());
            }
            if (!(authenticationProviderPluginModule instanceof IncomingTrustAuthenticationProviderPluginModule) || !((IncomingTrustAuthenticationProviderPluginModule)authenticationProviderPluginModule).incomingEnabled(applicationLink)) continue;
            configuredIncomingAuthenticationProviders.add(authenticationProviderPluginModule.getAuthenticationProviderClass());
        }
        boolean hasIncomingAuthenticationProviders = true;
        boolean hasOutgoingAuthenticationProviders = true;
        try {
            Manifest manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
            hasIncomingAuthenticationProviders = Sets.intersection((Set)Sets.newHashSet((Iterable)this.internalHostApplication.getSupportedInboundAuthenticationTypes()), (Set)manifest.getOutboundAuthenticationTypes()).size() > 0;
            hasOutgoingAuthenticationProviders = Sets.intersection((Set)Sets.newHashSet((Iterable)this.internalHostApplication.getSupportedOutboundAuthenticationTypes()), (Set)manifest.getInboundAuthenticationTypes()).size() > 0;
        }
        catch (ManifestNotFoundException manifest) {
            // empty catch block
        }
        WebFragmentContext context = new WebFragmentContext.Builder().applicationLink(applicationLink).build();
        WebItemEntityList webItems = this.webFragmentHelper.getWebItemsForLocation("applinks.application.link.list.operation", context);
        WebPanelEntityList webPanels = this.webFragmentHelper.getWebPanelsForLocation("applinks.application.link.list.operation", context);
        return new LinkAndAuthProviderEntity(this.toApplicationLinkEntity(applicationLink), configuredOutgoingAuthenticationProviders, configuredIncomingAuthenticationProviders, hasOutgoingAuthenticationProviders, hasIncomingAuthenticationProviders, webItems.getItems(), webPanels.getWebPanels(), this.getEntityTypeIdsForApplication(applicationLink), applicationLink.isSystem());
    }

    protected List<Callable<LinkAndAuthProviderEntity>> createJobs(Iterable<ApplicationLink> applicationLinks) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(applicationLinks, (Function)new Function<ApplicationLink, Callable<LinkAndAuthProviderEntity>>(){

            public Callable<LinkAndAuthProviderEntity> apply(final @Nullable ApplicationLink applicationLink) {
                return new CurrentContextAwareCallable<LinkAndAuthProviderEntity>(){

                    @Override
                    public LinkAndAuthProviderEntity callWithContext() throws Exception {
                        try {
                            return ListApplicationLinksUIResource.this.getLinkAndAuthProviderEntity(applicationLink);
                        }
                        catch (Exception e) {
                            LOG.error("Cannot retrieve link and provider entity for {}", (Object)applicationLink);
                            LOG.debug("Exception: ", (Throwable)e);
                            return null;
                        }
                    }
                };
            }
        }));
    }

    @GET
    public Response getApplicationLinks() {
        List<LinkAndAuthProviderEntity> links;
        try {
            links = this.retrieveLinkAndAuthProviderEntityList();
        }
        catch (Exception e) {
            LOG.error("Error occurred when retrieving list of application links", (Throwable)e);
            return RestUtil.serverError(this.i18nResolver.getText("applinks.error.retrieving.application.link.list", new Serializable[]{e.getMessage()}));
        }
        Collections.sort(links, new Comparator<LinkAndAuthProviderEntity>(){

            @Override
            public int compare(LinkAndAuthProviderEntity e1, LinkAndAuthProviderEntity e2) {
                int compareByType = e1.getApplication().getTypeId().get().compareTo(e2.getApplication().getTypeId().get());
                if (compareByType != 0) {
                    return compareByType;
                }
                return e1.getApplication().getName().compareTo(e2.getApplication().getName());
            }
        });
        return RestUtil.ok(new ListEntity<LinkAndAuthProviderEntity>(links));
    }

    protected List<LinkAndAuthProviderEntity> retrieveLinkAndAuthProviderEntityList() throws Exception {
        ArrayList links = new ArrayList();
        for (Future future : this.executor.invokeAll(this.createJobs(this.applicationLinkService.getApplicationLinks()))) {
            links.add(future.get());
        }
        return Lists.newArrayList((Iterable)Iterables.filter(links, (Predicate)Predicates.notNull()));
    }

    private Set<String> getEntityTypeIdsForApplication(ApplicationLink applicationLink) {
        return Sets.newHashSet((Iterable)Iterables.transform((Iterable)this.typeAccessor.getEnabledEntityTypesForApplicationType(applicationLink.getType()), (Function)new Function<EntityType, String>(){

            public String apply(@Nullable EntityType from) {
                return TypeId.getTypeId((EntityType)from).get();
            }
        }));
    }

    private static abstract class CurrentContextAwareCallable<T>
    implements Callable<T> {
        private final HttpContext httpContext = CurrentContext.getContext();
        private final HttpServletRequest httpServletRequest = CurrentContext.getHttpServletRequest();

        private CurrentContextAwareCallable() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final T call() throws Exception {
            HttpContext oldContext = CurrentContext.getContext();
            HttpServletRequest oldRequest = CurrentContext.getHttpServletRequest();
            CurrentContext.setContext(this.httpContext);
            CurrentContext.setHttpServletRequest(this.httpServletRequest);
            try {
                T t = this.callWithContext();
                return t;
            }
            finally {
                CurrentContext.setContext(oldContext);
                CurrentContext.setHttpServletRequest(oldRequest);
            }
        }

        public abstract T callWithContext() throws Exception;
    }
}

