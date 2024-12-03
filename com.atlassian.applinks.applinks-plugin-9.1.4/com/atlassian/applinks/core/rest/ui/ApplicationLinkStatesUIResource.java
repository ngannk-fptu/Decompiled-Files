/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ApplicationStatus
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.sun.jersey.api.core.HttpContext
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.concurrent.ConcurrentExecutor;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.rest.model.ApplicationLinkState;
import com.atlassian.applinks.core.rest.model.ApplicationLinkStateEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="listApplicationlinkstates")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplicationLinkStatesUIResource
extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLinkStatesUIResource.class);
    private final MutatingApplicationLinkService applicationLinkService;
    private final ManifestRetriever manifestRetriever;
    private final I18nResolver i18nResolver;
    private final ConcurrentExecutor executor;

    public ApplicationLinkStatesUIResource(MutatingApplicationLinkService applicationLinkService, ManifestRetriever manifestRetriever, I18nResolver i18nResolver, RestUrlBuilder restUrlBuilder, RequestFactory requestFactory, InternalTypeAccessor typeAccessor, ConcurrentExecutor executor) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.applicationLinkService = applicationLinkService;
        this.manifestRetriever = manifestRetriever;
        this.i18nResolver = i18nResolver;
        this.executor = executor;
    }

    @GET
    @Path(value="id/{id}")
    public Response getApplicationLinkState(@PathParam(value="id") ApplicationId id) {
        try {
            final MutableApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(id);
            if (applicationLink == null) {
                throw new Exception("Couldn't find application link");
            }
            Future<ApplicationLinkState> applicationLinkStateFuture = this.executor.submit(new CurrentContextAwareCallable<ApplicationLinkState>(){

                @Override
                public ApplicationLinkState callWithContext() throws Exception {
                    if (ApplicationLinkStatesUIResource.this.manifestRetriever.getApplicationStatus(applicationLink.getRpcUrl(), applicationLink.getType()) == ApplicationStatus.UNAVAILABLE) {
                        return ApplicationLinkState.OFFLINE;
                    }
                    try {
                        Manifest manifest = ApplicationLinkStatesUIResource.this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
                        if (!applicationLink.getId().equals((Object)manifest.getId())) {
                            if (manifest.getAppLinksVersion() != null && manifest.getAppLinksVersion().getMajor() >= 3) {
                                return ApplicationLinkState.UPGRADED_TO_UAL;
                            }
                            return ApplicationLinkState.UPGRADED;
                        }
                    }
                    catch (ManifestNotFoundException e) {
                        LOG.error("The {} application type failed to produce a Manifest for Application Link {}, so we cannot determine the link status.", (Object)TypeId.getTypeId((ApplicationType)applicationLink.getType()).toString(), (Object)applicationLink.getId().toString());
                    }
                    return ApplicationLinkState.OK;
                }
            });
            return RestUtil.ok(new ApplicationLinkStateEntity(applicationLinkStateFuture.get()));
        }
        catch (Exception e) {
            LOG.error("Error occurred when retrieving application link state", (Throwable)e);
            return RestUtil.serverError(this.i18nResolver.getText("applinks.error.retrieving.application.link.list", new Serializable[]{e.getMessage()}));
        }
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

