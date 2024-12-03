/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class TargetUrlNormalizer {
    private static final Logger log = LoggerFactory.getLogger(TargetUrlNormalizer.class);
    private static final Set<String> ILLEGAL_DESTINATION_URLS = ImmutableSet.of((Object)"/plugins/servlet/samlconsumer", (Object)"/plugins/servlet/sso-logout", (Object)"/plugins/servlet/oidc/initiate-login");
    protected final ApplicationProperties applicationProperties;
    @VisibleForTesting
    protected static final String PATH_PREFIX = "/a345/b342/c5462/";

    @Inject
    public TargetUrlNormalizer(@ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public URI getRelativeTargetUrl(String targetUrl) {
        if (targetUrl != null) {
            try {
                URI normalizedUri = new URI(targetUrl.replace(" ", "+")).normalize();
                URI relativizedAndNormalizedUri = this.relativizeUriIfNeeded(normalizedUri);
                String path = relativizedAndNormalizedUri.getPath();
                this.validatePathTraversal(targetUrl, path);
                if (!ILLEGAL_DESTINATION_URLS.contains(path)) {
                    return relativizedAndNormalizedUri;
                }
                log.debug("Requested destination url {} is not an allowed destination url, continuing without a destination url ", (Object)targetUrl);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("Error parsing provided url " + targetUrl + ", aborting", e);
            }
        }
        return null;
    }

    public URI removeContextPathFromUriIfNeeded(URI targetUri) {
        String contextPath = this.applicationProperties.getBaseUrl(UrlMode.RELATIVE);
        String urlAsString = targetUri.toString();
        if (urlAsString.startsWith(contextPath)) {
            return UriBuilder.fromUri((String)urlAsString.substring(contextPath.length(), urlAsString.length())).build(new Object[0]);
        }
        return targetUri;
    }

    private URI relativizeUriIfNeeded(URI targetUri) throws URISyntaxException {
        if (targetUri.isAbsolute() || Strings.emptyToNull((String)targetUri.getHost()) != null || targetUri.getPort() != -1 || Strings.emptyToNull((String)targetUri.getUserInfo()) != null || Strings.emptyToNull((String)targetUri.getAuthority()) != null) {
            URI relativizedUrl = UriBuilder.fromUri((String)"").replacePath(targetUri.getPath()).replaceQuery(targetUri.getRawQuery()).fragment(targetUri.getFragment()).build(new Object[0]);
            return this.removeContextPathFromUriIfNeeded(relativizedUrl);
        }
        return targetUri;
    }

    private void validatePathTraversal(String targetUrl, String path) {
        Preconditions.checkArgument((boolean)UriBuilder.fromPath((String)PATH_PREFIX).path(path).build(new Object[0]).normalize().getPath().startsWith(PATH_PREFIX), (Object)("Requested path traversal outside the context path " + targetUrl + ", aborting"));
    }
}

