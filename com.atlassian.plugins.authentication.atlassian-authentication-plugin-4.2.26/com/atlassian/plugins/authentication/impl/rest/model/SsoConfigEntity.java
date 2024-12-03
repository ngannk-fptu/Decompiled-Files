/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.impl.rest.model.ISO8601DateDeserializer;
import com.atlassian.plugins.authentication.impl.rest.model.ISO8601DateSerializer;
import java.time.ZonedDateTime;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class SsoConfigEntity {
    @JsonProperty(value="show-login-form")
    private Boolean showLoginForm;
    @JsonProperty(value="enable-authentication-fallback")
    private Boolean enableAuthenticationFallback;
    @JsonProperty(value="show-login-form-for-jsm")
    private Boolean showLoginFormForJsm;
    @JsonProperty(value="last-updated")
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    @JsonSerialize(using=ISO8601DateSerializer.class)
    private ZonedDateTime lastUpdated;
    @JsonProperty(value="discovery-refresh-cron")
    private String discoveryRefreshCron;

    private SsoConfigEntity() {
    }

    public SsoConfigEntity(SsoConfig config) {
        this.showLoginForm = config.getShowLoginForm();
        this.enableAuthenticationFallback = config.enableAuthenticationFallback();
        this.discoveryRefreshCron = config.getDiscoveryRefreshCron();
        this.lastUpdated = config.getLastUpdated();
        this.showLoginFormForJsm = config.getShowLoginFormForJsm();
    }

    public Boolean getShowLoginForm() {
        return this.showLoginForm;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public Boolean getEnableAuthenticationFallback() {
        return this.enableAuthenticationFallback;
    }

    public String getDiscoveryRefreshCron() {
        return this.discoveryRefreshCron;
    }

    public Boolean getShowLoginFormForJsm() {
        return this.showLoginFormForJsm;
    }

    public static interface Config {
        public static final String SHOW_LOGIN_FORM = "show-login-form";
        public static final String ENABLE_AUTHENTICATION_FALLBACK = "enable-authentication-fallback";
        public static final String SHOW_LOGIN_FORM_JSM = "show-login-form-for-jsm";

        public static interface Oidc {
            public static final String DISCOVERY_REFRESH_CRON = "discovery-refresh-cron";
        }
    }
}

