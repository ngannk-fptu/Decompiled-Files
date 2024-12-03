/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.cors.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.cors.auth.CorsService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultCorsService
implements CorsService {
    public static final String KEY_ALLOWS_CREDENTIALS = "allowsCredentials";
    private final AuthenticationConfigurationManager configurationManager;
    private final ApplicationLinkService linkService;

    @Autowired
    public DefaultCorsService(ApplicationLinkService applicationLinkService, AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.configurationManager = authenticationConfigurationManager;
        this.linkService = applicationLinkService;
    }

    @Override
    public boolean allowsCredentials(ApplicationLink link) {
        Map configuration = this.configurationManager.getConfiguration(link.getId(), CorsAuthenticationProvider.class);
        return configuration != null && "true".equals(configuration.get(KEY_ALLOWS_CREDENTIALS));
    }

    @Override
    public void disableCredentials(ApplicationLink link) {
        this.configurationManager.unregisterProvider(link.getId(), CorsAuthenticationProvider.class);
    }

    @Override
    public void enableCredentials(ApplicationLink link) {
        Map<String, String> configuration = Collections.singletonMap(KEY_ALLOWS_CREDENTIALS, "true");
        this.configurationManager.registerProvider(link.getId(), CorsAuthenticationProvider.class, configuration);
    }

    @Override
    public Collection<ApplicationLink> getApplicationLinksByOrigin(String origin) {
        URI uri;
        try {
            uri = new URI(origin).normalize();
        }
        catch (URISyntaxException e) {
            return Collections.emptySet();
        }
        return this.getApplicationLinksByUri(uri);
    }

    @Override
    public Collection<ApplicationLink> getApplicationLinksByUri(URI uri) {
        ArrayList<ApplicationLink> matches = new ArrayList<ApplicationLink>();
        for (ApplicationLink link : this.linkService.getApplicationLinks()) {
            if (!this.matchesOrigin(uri, link.getRpcUrl())) continue;
            matches.add(link);
        }
        return matches;
    }

    @Override
    public Collection<ApplicationLink> getRequiredApplicationLinksByOrigin(String origin) {
        Collection<ApplicationLink> links = this.getApplicationLinksByOrigin(origin);
        Validate.notEmpty(links, (String)("Origin [" + origin + "] is required to match at least one ApplicationLink"), (Object[])new Object[0]);
        return links;
    }

    private boolean matchesOrigin(URI origin, URI link) {
        link = link.normalize();
        return StringUtils.equalsIgnoreCase((CharSequence)origin.getHost(), (CharSequence)link.getHost()) && this.normalizePort(origin) == this.normalizePort(link) && StringUtils.equalsIgnoreCase((CharSequence)origin.getScheme(), (CharSequence)link.getScheme());
    }

    private int normalizePort(URI uri) {
        int port = uri.getPort();
        if (port == -1) {
            port = RequestUtil.getDefaultPort((String)uri.getScheme());
        }
        return port;
    }
}

