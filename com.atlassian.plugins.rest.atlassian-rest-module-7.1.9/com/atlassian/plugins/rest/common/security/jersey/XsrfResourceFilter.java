/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.method.Methods
 *  com.atlassian.http.mime.BrowserUtils
 *  com.atlassian.http.mime.UserAgentUtil$BrowserFamily
 *  com.atlassian.http.mime.UserAgentUtilImpl
 *  com.atlassian.http.url.SameOrigin
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.xsrf.XsrfRequestValidator
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.http.method.Methods;
import com.atlassian.http.mime.BrowserUtils;
import com.atlassian.http.mime.UserAgentUtil;
import com.atlassian.http.mime.UserAgentUtilImpl;
import com.atlassian.http.url.SameOrigin;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.rest.common.security.CorsHeaders;
import com.atlassian.plugins.rest.common.security.XsrfCheckFailedException;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaultsModuleDescriptor;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.xsrf.XsrfRequestValidator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsrfResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    public static final String TOKEN_HEADER = "X-Atlassian-Token";
    public static final String NO_CHECK = "no-check";
    private static final ImmutableSet<String> XSRFABLE_TYPES = ImmutableSet.of((Object)"application/x-www-form-urlencoded", (Object)"multipart/form-data", (Object)"text/plain");
    private static final ImmutableSet<String> BROWSER_EXTENSION_ORIGINS = ImmutableSet.of((Object)"chrome-extension", (Object)"safari-extension");
    private static final Logger log = LoggerFactory.getLogger(XsrfResourceFilter.class);
    private static final Cache<String, Boolean> XSRF_NOT_ENFORCED_RESOURCE_CACHE = CacheBuilder.newBuilder().maximumSize(1000L).build();
    private HttpContext httpContext;
    private XsrfRequestValidator xsrfRequestValidator;
    private PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> pluginModuleTracker;
    private Response.Status failureStatus = Response.Status.FORBIDDEN;

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void setXsrfRequestValidator(XsrfRequestValidator xsrfRequestValidator) {
        this.xsrfRequestValidator = xsrfRequestValidator;
    }

    public void setPluginModuleTracker(PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> pluginModuleTracker) {
        this.pluginModuleTracker = pluginModuleTracker;
    }

    public void setFailureStatus(Response.Status failureStatus) {
        if (failureStatus != Response.Status.FORBIDDEN && failureStatus != Response.Status.NOT_FOUND) {
            throw new IllegalArgumentException("Only FORBIDDEN and NOT_FOUND status are valid arguments.");
        }
        this.failureStatus = failureStatus;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (this.passesAllXsrfChecks(request)) {
            return request;
        }
        throw new XsrfCheckFailedException(this.failureStatus);
    }

    private boolean passesAllXsrfChecks(ContainerRequest request) {
        HttpServletRequest httpRequest = XsrfResourceFilter.getRequestOrNull(this.httpContext);
        String method = httpRequest != null && httpRequest.getMethod() != null ? httpRequest.getMethod() : request.getMethod();
        boolean isMethodMutative = Methods.isMutative((String)method);
        boolean isPostRequest = XsrfResourceFilter.isPostRequest(method);
        if (isMethodMutative && this.isLikelyToBeFromBrowser(request)) {
            boolean passesOriginChecks = this.passesAdditionalBrowserChecks(request);
            if (isPostRequest && !passesOriginChecks) {
                return false;
            }
            if (!isPostRequest) {
                if (!passesOriginChecks) {
                    this.logXsrfFailureButNotBeingEnforced(request, log);
                }
                return true;
            }
        }
        if (this.isXsrfable(method, request.getMediaType())) {
            boolean passes;
            boolean bl = passes = this.passesStandardXsrfChecks(httpRequest) || this.hasDeprecatedHeaderValue(request);
            if (passes) {
                return true;
            }
            if (isMethodMutative && !isPostRequest) {
                this.logXsrfFailureButNotBeingEnforced(request, log);
                return true;
            }
            log.warn("XSRF checks failed for request: {} , origin: {} , referrer: {}", new Object[]{StringUtils.substringBefore((String)request.getRequestUri().toString(), (String)"?"), request.getHeaderValue(CorsHeaders.ORIGIN.value()), XsrfResourceFilter.getSanitisedReferrer(request)});
            return false;
        }
        return true;
    }

    void logXsrfFailureButNotBeingEnforced(ContainerRequest request, Logger logger2) {
        String key = request.getPath();
        if (key != null && XSRF_NOT_ENFORCED_RESOURCE_CACHE.getIfPresent((Object)key) == null) {
            logger2.warn("XSRF failure not being enforced for request: {} , origin: {} , referrer: {}, method: {}", new Object[]{StringUtils.substringBefore((String)request.getRequestUri().toString(), (String)"?"), request.getHeaderValue(CorsHeaders.ORIGIN.value()), XsrfResourceFilter.getSanitisedReferrer(request), request.getMethod()});
            XSRF_NOT_ENFORCED_RESOURCE_CACHE.put((Object)key, (Object)Boolean.TRUE);
        }
    }

    private boolean passesStandardXsrfChecks(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            return false;
        }
        return this.xsrfRequestValidator.validateRequestPassesXsrfChecks(httpServletRequest);
    }

    boolean isOriginABrowserExtension(String origin) {
        if (StringUtils.isEmpty((CharSequence)origin)) {
            return false;
        }
        try {
            URI originUri = new URI(origin);
            return BROWSER_EXTENSION_ORIGINS.contains((Object)originUri.getScheme()) && !originUri.isOpaque();
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    @VisibleForTesting
    protected boolean passesAdditionalBrowserChecks(ContainerRequest request) {
        URI uri;
        String origin = request.getHeaderValue(CorsHeaders.ORIGIN.value());
        String referrer = XsrfResourceFilter.getSanitisedReferrer(request);
        if (this.isSameOrigin(referrer, uri = request.getRequestUri())) {
            return true;
        }
        if (this.isSameOrigin(origin, uri)) {
            return true;
        }
        if (this.isOriginABrowserExtension(origin)) {
            return true;
        }
        boolean requestContainsCredentials = XsrfResourceFilter.containsCredentials(request);
        boolean requestAllowedViaCors = this.isAllowedViaCors(origin, requestContainsCredentials);
        if (requestAllowedViaCors) {
            return true;
        }
        if (request.getMethod() != null && XsrfResourceFilter.isPostRequest(request.getMethod())) {
            log.warn("Additional XSRF checks failed for request: {} , origin: {} , referrer: {} , credentials in request: {} , allowed via CORS: {}", new Object[]{StringUtils.substringBefore((String)uri.toString(), (String)"?"), origin, referrer, requestContainsCredentials, requestAllowedViaCors});
        }
        return false;
    }

    boolean isXsrfable(String method, MediaType mediaType) {
        return method.equals("GET") || Methods.isMutative((String)method) && (mediaType == null || XSRFABLE_TYPES.contains((Object)XsrfResourceFilter.mediaTypeToString(mediaType)));
    }

    private boolean hasDeprecatedHeaderValue(ContainerRequest request) {
        String tokenHeader = request.getHeaderValue(TOKEN_HEADER);
        if (tokenHeader == null) {
            return false;
        }
        String normalisedTokenHeader = tokenHeader.toLowerCase(Locale.ENGLISH);
        if (normalisedTokenHeader.equals("nocheck")) {
            log.warn("Use of the 'nocheck' value for {} has been deprecated since rest 3.0.0. Please use a value of 'no-check' instead.", (Object)TOKEN_HEADER);
            return true;
        }
        return false;
    }

    private boolean isSameOrigin(String uri, URI origin) {
        try {
            return StringUtils.isNotEmpty((CharSequence)uri) && SameOrigin.isSameOrigin((URI)new URI(uri), (URI)origin);
        }
        catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private boolean isAllowedViaCors(String originUri, boolean withCredentials) {
        if (originUri == null) {
            return false;
        }
        return StreamSupport.stream(this.pluginModuleTracker.getModules().spliterator(), false).anyMatch(delegate -> delegate.allowsOrigin(originUri) && (!withCredentials || delegate.allowsCredentials(originUri)));
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    private static boolean containsCredentials(ContainerRequest request) {
        return XsrfResourceFilter.containsCookies(request) || XsrfResourceFilter.containsHttpAuthHeader(request);
    }

    private static boolean containsCookies(ContainerRequest request) {
        return !request.getCookies().isEmpty();
    }

    private static boolean containsHttpAuthHeader(ContainerRequest request) {
        return StringUtils.isNotEmpty((CharSequence)request.getHeaderValue("Authorization"));
    }

    static boolean isPostRequest(String method) {
        return method.equals("POST");
    }

    boolean isLikelyToBeFromBrowser(ContainerRequest request) {
        String userAgent = request.getHeaderValue("User-Agent");
        UserAgentUtil.BrowserFamily browserFamily = new UserAgentUtilImpl().getBrowserFamily(userAgent);
        if ((this.passesStandardXsrfChecks(XsrfResourceFilter.getRequestOrNull(this.httpContext)) || this.hasDeprecatedHeaderValue(request)) && BrowserUtils.isIE((String)userAgent)) {
            return false;
        }
        return !browserFamily.equals((Object)UserAgentUtil.BrowserFamily.UKNOWN);
    }

    private static HttpServletRequest getRequestOrNull(HttpContext httpContext) {
        return httpContext == null ? null : httpContext.getRequest();
    }

    private static String mediaTypeToString(MediaType mediaType) {
        return mediaType.getType().toLowerCase(Locale.ENGLISH) + "/" + mediaType.getSubtype().toLowerCase(Locale.ENGLISH);
    }

    private static String getSanitisedReferrer(ContainerRequest request) {
        return StringUtils.substringBefore((String)request.getHeaderValue("Referer"), (String)"?");
    }
}

