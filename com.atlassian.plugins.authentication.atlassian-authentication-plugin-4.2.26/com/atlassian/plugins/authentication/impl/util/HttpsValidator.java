/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HttpsValidator {
    private final ApplicationProperties applicationProperties;
    private final DarkFeatureManager darkFeatureManager;

    @Inject
    public HttpsValidator(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport DarkFeatureManager darkFeatureManager) {
        this.applicationProperties = applicationProperties;
        this.darkFeatureManager = darkFeatureManager;
    }

    public boolean isBaseUrlSecure() {
        return this.isBaseUrlHttps() || !this.isHttpsRequired();
    }

    public boolean isHttpsRequired() {
        boolean skipRequirement = this.darkFeatureManager.isEnabledForAllUsers("atlassian.authentication.sso.skip.https.requirement").orElseGet(() -> this.darkFeatureManager.isEnabledForAllUsers("atlassian.authentication.saml.sso.skip.https.requirement").orElse(false));
        return !skipRequirement;
    }

    public boolean isBaseUrlHttps() {
        try {
            return "https".equals(new URI(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL)).getScheme());
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static interface DarkFeature {
        public static final String SKIP_HTTPS_REQUIREMENT = "atlassian.authentication.sso.skip.https.requirement";
        public static final String SKIP_HTTPS_REQUIREMENT_SAML = "atlassian.authentication.saml.sso.skip.https.requirement";
    }
}

