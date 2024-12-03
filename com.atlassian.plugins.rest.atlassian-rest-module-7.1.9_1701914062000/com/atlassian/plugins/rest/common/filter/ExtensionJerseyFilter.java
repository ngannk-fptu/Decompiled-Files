/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.plugins.rest.common.filter;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

@Provider
public class ExtensionJerseyFilter
implements ContainerRequestFilter {
    private static final String DOT = ".";
    private final Collection<Pattern> pathExcludePatterns;
    @TenantAware(value=TenancyScope.TENANTLESS)
    static final Map<String, String> EXTENSION_TO_ACCEPT_HEADER = new ImmutableMap.Builder().put((Object)"txt", (Object)"text/plain").put((Object)"htm", (Object)"text/html").put((Object)"html", (Object)"text/html").put((Object)"json", (Object)"application/json").put((Object)"xml", (Object)"application/xml").put((Object)"atom", (Object)"application/atom+xml").build();

    public ExtensionJerseyFilter() {
        this.pathExcludePatterns = new LinkedList<Pattern>();
    }

    public ExtensionJerseyFilter(Collection<String> pathExcludePatterns) {
        Validate.notNull(pathExcludePatterns);
        this.pathExcludePatterns = this.compilePatterns(pathExcludePatterns);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String absoluteUri = request.getAbsolutePath().toString();
        String extension = StringUtils.substringAfterLast((String)absoluteUri, (String)DOT);
        if (this.shouldFilter("/" + StringUtils.difference((String)request.getBaseUri().toString(), (String)absoluteUri), extension)) {
            request.getRequestHeaders().putSingle("Accept", EXTENSION_TO_ACCEPT_HEADER.get(extension));
            String absoluteUriWithoutExtension = StringUtils.substringBeforeLast((String)absoluteUri, (String)DOT);
            request.setUris(request.getBaseUri(), this.getRequestUri(absoluteUriWithoutExtension, request.getQueryParameters()));
        }
        return request;
    }

    private boolean shouldFilter(String restPath, String extension) {
        for (Pattern pattern : this.pathExcludePatterns) {
            if (!pattern.matcher(restPath).matches()) continue;
            return false;
        }
        return EXTENSION_TO_ACCEPT_HEADER.containsKey(extension);
    }

    private URI getRequestUri(String absoluteUriWithoutExtension, Map<String, List<String>> queryParams) {
        UriBuilder requestUriBuilder = UriBuilder.fromUri(absoluteUriWithoutExtension);
        for (Map.Entry<String, List<String>> queryParamEntry : queryParams.entrySet()) {
            for (String value : queryParamEntry.getValue()) {
                requestUriBuilder.queryParam(queryParamEntry.getKey(), value);
            }
        }
        return requestUriBuilder.build(new Object[0]);
    }

    private Collection<Pattern> compilePatterns(Collection<String> pathExcludePatterns) {
        LinkedList<Pattern> patterns = new LinkedList<Pattern>();
        for (String pattern : pathExcludePatterns) {
            patterns.add(Pattern.compile(pattern));
        }
        return patterns;
    }
}

