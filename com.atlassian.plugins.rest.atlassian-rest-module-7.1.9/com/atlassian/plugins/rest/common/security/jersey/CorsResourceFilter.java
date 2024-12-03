/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.rest.common.security.CorsHeaders;
import com.atlassian.plugins.rest.common.security.CorsPreflightCheckCompleteException;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaultsModuleDescriptor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorsResourceFilter
implements ResourceFilter,
ContainerRequestFilter,
ContainerResponseFilter {
    private static final String CORS_PREFLIGHT_FAILED = "Cors-Preflight-Failed";
    private static final String CORS_PREFLIGHT_SUCCEEDED = "Cors-Preflight-Succeeded";
    public static final String CORS_PREFLIGHT_REQUESTED = "Cors-Preflight-Requested";
    private static final Logger log = LoggerFactory.getLogger(CorsResourceFilter.class);
    private final PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> pluginModuleTracker;
    private final String allowMethod;

    public CorsResourceFilter(PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> pluginModuleTracker, String allowMethod) {
        this.allowMethod = allowMethod;
        this.pluginModuleTracker = pluginModuleTracker;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (!request.getProperties().containsKey(CORS_PREFLIGHT_REQUESTED)) {
            return request;
        }
        Iterable defaults = this.pluginModuleTracker.getModules();
        try {
            String origin = this.validateSingleOriginInWhitelist(defaults, request);
            Iterable<CorsDefaults> defaultsWithAllowedOrigin = CorsResourceFilter.allowsOrigin(defaults, origin);
            Response.ResponseBuilder response = Response.ok();
            this.validateAccessControlRequestMethod(this.allowMethod, request);
            Set<String> allowedRequestHeaders = CorsResourceFilter.getAllowedRequestHeaders(defaultsWithAllowedOrigin, origin);
            this.validateAccessControlRequestHeaders(allowedRequestHeaders, request);
            this.addAccessControlAllowOrigin(response, origin);
            this.conditionallyAddAccessControlAllowCredentials(response, origin, defaultsWithAllowedOrigin);
            this.addAccessControlMaxAge(response);
            this.addAccessControlAllowMethods(response, this.allowMethod);
            this.addAccessControlAllowHeaders(response, allowedRequestHeaders);
            request.getProperties().put(CORS_PREFLIGHT_SUCCEEDED, "true");
            throw new CorsPreflightCheckCompleteException(response.build());
        }
        catch (PreflightFailedException ex) {
            Response.ResponseBuilder response = Response.ok();
            request.getProperties().put(CORS_PREFLIGHT_FAILED, "true");
            log.info("CORS preflight failed: {}", (Object)ex.getMessage());
            throw new CorsPreflightCheckCompleteException(response.build());
        }
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse containerResponse) {
        if (request.getProperties().containsKey(CORS_PREFLIGHT_FAILED) || request.getProperties().containsKey(CORS_PREFLIGHT_SUCCEEDED) || CorsResourceFilter.extractOrigin(request) == null) {
            return containerResponse;
        }
        Iterable defaults = this.pluginModuleTracker.getModules();
        try {
            String origin = this.validateSingleOriginInWhitelist(defaults, request);
            Iterable<CorsDefaults> defaultsWithAllowedOrigin = CorsResourceFilter.allowsOrigin(defaults, origin);
            Response.ResponseBuilder response = Response.fromResponse(containerResponse.getResponse());
            this.addAccessControlAllowOrigin(response, origin);
            this.conditionallyAddAccessControlAllowCredentials(response, origin, defaultsWithAllowedOrigin);
            this.addAccessControlExposeHeaders(response, CorsResourceFilter.getAllowedResponseHeaders(defaultsWithAllowedOrigin, origin));
            containerResponse.setResponse(response.build());
            return containerResponse;
        }
        catch (PreflightFailedException ex) {
            log.info("Unable to add CORS headers to response: {}", (Object)ex.getMessage());
            return containerResponse;
        }
    }

    private void addAccessControlExposeHeaders(Response.ResponseBuilder response, Set<String> allowedHeaders) {
        response.header(CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS.value(), String.join((CharSequence)", ", allowedHeaders));
    }

    private void addAccessControlAllowHeaders(Response.ResponseBuilder response, Set<String> allowedHeaders) {
        response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS.value(), String.join((CharSequence)", ", allowedHeaders));
    }

    private void addAccessControlAllowMethods(Response.ResponseBuilder response, String allowMethod) {
        response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS.value(), allowMethod);
    }

    private void addAccessControlMaxAge(Response.ResponseBuilder response) {
        response.header(CorsHeaders.ACCESS_CONTROL_MAX_AGE.value(), 3600);
    }

    private void addAccessControlAllowOrigin(Response.ResponseBuilder response, String origin) {
        response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.value(), origin);
    }

    private void conditionallyAddAccessControlAllowCredentials(Response.ResponseBuilder response, String origin, Iterable<CorsDefaults> defaultsWithAllowedOrigin) {
        if (CorsResourceFilter.anyAllowsCredentials(defaultsWithAllowedOrigin, origin)) {
            response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS.value(), "true");
        }
    }

    private void validateAccessControlRequestHeaders(Set<String> allowedHeaders, ContainerRequest request) throws PreflightFailedException {
        List<String> requestedHeaders = request.getRequestHeader(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS.value());
        requestedHeaders = requestedHeaders != null ? requestedHeaders : Collections.emptyList();
        HashSet<String> flatRequestedHeaders = new HashSet<String>();
        for (String requestedHeader : requestedHeaders) {
            flatRequestedHeaders.addAll(Arrays.asList(requestedHeader.toLowerCase(Locale.US).trim().split("\\s*,\\s*")));
        }
        ImmutableSet allowedHeadersLowerCase = ImmutableSet.copyOf((Collection)allowedHeaders.stream().map(from -> from.toLowerCase(Locale.US)).collect(Collectors.toList()));
        Sets.SetView difference = Sets.difference(flatRequestedHeaders, (Set)allowedHeadersLowerCase);
        if (!difference.isEmpty()) {
            throw new PreflightFailedException("Unexpected headers in CORS request: " + Lists.newArrayList((Iterable)difference));
        }
    }

    private void validateAccessControlRequestMethod(String allowMethod, ContainerRequest request) throws PreflightFailedException {
        String requestedMethod = request.getHeaderValue(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD.value());
        if (!allowMethod.equals(requestedMethod)) {
            throw new PreflightFailedException("Invalid method: " + requestedMethod);
        }
    }

    private String validateSingleOriginInWhitelist(Iterable<CorsDefaults> defaults, ContainerRequest request) throws PreflightFailedException {
        String origin = CorsResourceFilter.extractOrigin(request);
        this.validateOriginAsUri(origin);
        if (Iterables.isEmpty(CorsResourceFilter.allowsOrigin(defaults, origin))) {
            throw new PreflightFailedException("Origin '" + origin + "' not in whitelist");
        }
        return origin;
    }

    private void validateOriginAsUri(String origin) throws PreflightFailedException {
        try {
            URI originUri = URI.create(origin);
            if (originUri.isOpaque() || !originUri.isAbsolute()) {
                throw new IllegalArgumentException("The origin URI must be absolute and not opaque.");
            }
        }
        catch (IllegalArgumentException ex) {
            throw new PreflightFailedException("Origin '" + origin + "' is not a valid URI");
        }
    }

    public static String extractOrigin(ContainerRequest request) {
        return request.getHeaderValue(CorsHeaders.ORIGIN.value());
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return this;
    }

    private static Iterable<CorsDefaults> allowsOrigin(Iterable<CorsDefaults> delegates, String uri) {
        return StreamSupport.stream(delegates.spliterator(), false).filter((? super T delegate) -> delegate.allowsOrigin(uri)).collect(Collectors.toList());
    }

    private static boolean anyAllowsCredentials(Iterable<CorsDefaults> delegatesWhichAllowOrigin, String uri) {
        for (CorsDefaults defs : delegatesWhichAllowOrigin) {
            if (!defs.allowsCredentials(uri)) continue;
            return true;
        }
        return false;
    }

    private static Set<String> getAllowedRequestHeaders(Iterable<CorsDefaults> delegatesWhichAllowOrigin, String uri) {
        HashSet result = Sets.newHashSet();
        for (CorsDefaults defs : delegatesWhichAllowOrigin) {
            result.addAll(defs.getAllowedRequestHeaders(uri));
        }
        return result;
    }

    private static Set<String> getAllowedResponseHeaders(Iterable<CorsDefaults> delegatesWithAllowedOrigin, String uri) {
        HashSet result = Sets.newHashSet();
        for (CorsDefaults defs : delegatesWithAllowedOrigin) {
            result.addAll(defs.getAllowedResponseHeaders(uri));
        }
        return result;
    }

    private static class PreflightFailedException
    extends Exception {
        private PreflightFailedException(String message) {
            super(message);
        }
    }
}

