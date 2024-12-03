/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatusBuilder;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckSupplier;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthStatus;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportHealthStatusBuilder
implements HealthCheckStatusBuilder {
    private final I18nResolver i18nResolver;
    private final HelpPathResolver helpPathResolver;
    private final SupportHealthCheckSupplier shcSupplier;
    private final Application application;
    private final ApplicationProperties appProperties;
    private final ClusterService clusterService;

    @Autowired
    public SupportHealthStatusBuilder(@Nonnull I18nResolver i18nResolver, @Nonnull ApplicationProperties applicationProperties, @Nonnull HelpPathResolver helpPathResolver, @Nonnull SupportHealthCheckSupplier shcSupplier, ClusterService clusterService) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.helpPathResolver = Objects.requireNonNull(helpPathResolver);
        this.shcSupplier = Objects.requireNonNull(shcSupplier);
        this.appProperties = Objects.requireNonNull(applicationProperties);
        this.clusterService = clusterService;
        this.application = Application.byAppDisplayName(this.appProperties.getDisplayName());
    }

    @Override
    public SupportHealthStatus ok(@Nonnull SupportHealthCheck healthCheck, String key, Serializable ... objects) {
        return this.buildStatus(healthCheck, SupportHealthStatus.Severity.UNDEFINED, key, objects);
    }

    @Override
    public SupportHealthStatus warning(@Nonnull SupportHealthCheck healthCheck, String key, Serializable ... objects) {
        return this.buildStatus(healthCheck, SupportHealthStatus.Severity.WARNING, key, objects);
    }

    @Override
    public SupportHealthStatus major(@Nonnull SupportHealthCheck healthCheck, String key, Serializable ... objects) {
        return this.buildStatus(healthCheck, SupportHealthStatus.Severity.MAJOR, key, objects);
    }

    @Override
    public SupportHealthStatus critical(@Nonnull SupportHealthCheck healthCheck, String key, Serializable ... objects) {
        return this.buildStatus(healthCheck, SupportHealthStatus.Severity.CRITICAL, key, objects);
    }

    @Override
    public SupportHealthStatus disabled(@Nonnull SupportHealthCheck healthCheck, String key, Serializable ... objects) {
        return this.buildStatus(healthCheck, SupportHealthStatus.Severity.DISABLED, key, objects);
    }

    private String getHelpPathUrl(@Nonnull SupportHealthCheck healthCheck) {
        String hcHelpUrl = this.shcSupplier.getHelpPathKey(healthCheck).orElseThrow(IllegalArgumentException::new);
        if (hcHelpUrl.startsWith("/")) {
            return UriBuilder.fromPath((String)this.appProperties.getBaseUrl(UrlMode.RELATIVE)).path(hcHelpUrl).build(new Object[0]).toString();
        }
        return Optional.ofNullable(this.helpPathResolver.getHelpPath(hcHelpUrl)).map(HelpPath::getUrl).orElseThrow(() -> new IllegalArgumentException("Could not resolve help url for key '" + hcHelpUrl + "'"));
    }

    private DefaultSupportHealthStatus buildStatus(String nodeId, @Nonnull SupportHealthStatus.Severity severity, @Nonnull String helpUrl, String key, Serializable ... objects) {
        Objects.requireNonNull(severity);
        return new Builder().nodeId(nodeId).severity(severity).helpUrl(helpUrl).i18nMessage(key, objects).build();
    }

    public DefaultSupportHealthStatus buildStatus(@Nonnull SupportHealthCheck healthCheck, @Nonnull SupportHealthStatus.Severity severity, String key, Serializable ... objects) {
        Objects.requireNonNull(healthCheck);
        return this.builder(healthCheck).severity(severity).i18nMessage(key, objects).build();
    }

    public Builder builder(SupportHealthCheck healthCheck) {
        return new Builder().nodeId(healthCheck.isNodeSpecific() ? (String)this.clusterService.getCurrentNodeId().orElse(null) : null).helpUrl(this.getHelpPathUrl(healthCheck));
    }

    private String getMessage(String key, Serializable ... objects) {
        return this.i18nResolver.getText(key, objects);
    }

    public class Builder {
        private String nodeId;
        private SupportHealthStatus.Severity severity = SupportHealthStatus.Severity.UNDEFINED;
        private String helpUrl;
        private String message;
        private Set<SupportHealthStatus.Link> additionalLinks = new LinkedHashSet<SupportHealthStatus.Link>();

        private Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder severity(SupportHealthStatus.Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder i18nMessage(String key, Serializable ... args) {
            this.message = SupportHealthStatusBuilder.this.getMessage(key, args);
            return this;
        }

        public Builder helpUrl(String helpUrl) {
            this.helpUrl = helpUrl;
            return this;
        }

        public Builder additionalLink(String displayName, String url) {
            this.additionalLinks.add(new DefaultSupportHealthStatus.DefaultLink(displayName, url));
            return this;
        }

        public DefaultSupportHealthStatus build() {
            return new DefaultSupportHealthStatus(SupportHealthStatus.Severity.UNDEFINED.equals((Object)this.severity), this.message, System.currentTimeMillis(), SupportHealthStatusBuilder.this.application, this.nodeId, this.severity, this.helpUrl, this.additionalLinks);
        }
    }
}

