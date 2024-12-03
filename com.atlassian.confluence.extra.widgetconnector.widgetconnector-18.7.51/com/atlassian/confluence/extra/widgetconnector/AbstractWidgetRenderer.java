/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.InternetDomainName
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.google.common.net.InternetDomainName;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWidgetRenderer
implements WidgetRenderer {
    private static final Logger log = LoggerFactory.getLogger(AbstractWidgetRenderer.class);
    private static final String TRUSTED_DOMAINS_REGISTRY = "com/atlassian/confluence/extra/widgetconnector/trusted-domains.properties";
    protected static final Map<String, Set<String>> trustedDomains = AbstractWidgetRenderer.loadTrustedDomains();

    private static Map<String, Set<String>> loadTrustedDomains() {
        Properties properties = new Properties();
        try {
            properties.load(AbstractWidgetRenderer.class.getClassLoader().getResourceAsStream(TRUSTED_DOMAINS_REGISTRY));
            return properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> Arrays.stream(e.getValue().toString().split("\\s*,\\s*")).collect(Collectors.toSet())));
        }
        catch (IOException e2) {
            String errMsg = String.format("Failed loading domains registry from '%s'", TRUSTED_DOMAINS_REGISTRY);
            log.error(errMsg, (Throwable)e2);
            throw new RuntimeException(errMsg, e2);
        }
    }

    protected final String getTopLevelDomainName(String url) {
        try {
            URI uri = URI.create(url).normalize();
            return InternetDomainName.from((String)uri.getHost()).topDomainUnderRegistrySuffix().toString();
        }
        catch (Exception e) {
            log.debug(String.format("Failed retrieving top-level domain name from URL: %s", url), (Throwable)e);
            return null;
        }
    }

    protected String getTrustedDomainsKey() {
        return this.getServiceName();
    }

    @Override
    public boolean matches(String url) {
        String host = this.getTopLevelDomainName(url);
        if (host != null) {
            return trustedDomains.get(this.getTrustedDomainsKey()).contains(host);
        }
        return false;
    }
}

