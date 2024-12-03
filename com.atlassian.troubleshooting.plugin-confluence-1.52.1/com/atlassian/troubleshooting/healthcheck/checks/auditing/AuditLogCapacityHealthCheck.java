/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.auditing;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.checks.auditing.AuditLogCapacity;
import com.atlassian.troubleshooting.http.HttpClientFactory;
import com.atlassian.troubleshooting.stp.util.ObjectMapperFactory;
import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditLogCapacityHealthCheck
implements SupportHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(AuditLogCapacityHealthCheck.class);
    private static final double THRESHOLD_WARNING_PERCENTAGE = 0.8;
    private static final double THRESHOLD_ERROR_PERCENTAGE = 0.99;
    private static final String AUDIT_CAPACITY_REST_ENDPOINT = "rest/auditing/latest/statistics/database/usage";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final HttpClient httpClient;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public AuditLogCapacityHealthCheck(SupportHealthStatusBuilder supportHealthStatusBuilder, ApplicationProperties applicationProperties, HttpClientFactory httpClientFactory) {
        this.supportHealthStatusBuilder = Objects.requireNonNull(supportHealthStatusBuilder);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.httpClient = Objects.requireNonNull(httpClientFactory).newHttpClient(5000);
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        double currentUsageInPercentage = this.getAuditLogUsageByRestAPI().map(AuditLogCapacity::getUsage).orElse(-1.0);
        if (currentUsageInPercentage >= 0.99) {
            return this.supportHealthStatusBuilder.major(this, "healthcheck.audit.log.capacity.error", new Serializable[0]);
        }
        if (currentUsageInPercentage >= 0.8) {
            return this.supportHealthStatusBuilder.warning(this, "healthcheck.audit.log.capacity.warning", new Serializable[0]);
        }
        if (currentUsageInPercentage >= 0.0) {
            return this.supportHealthStatusBuilder.ok(this, "healthcheck.audit.log.capacity.ok", new Serializable[0]);
        }
        return this.supportHealthStatusBuilder.ok(this, "healthcheck.audit.log.capacity.notsure", new Serializable[0]);
    }

    private Optional<AuditLogCapacity> getAuditLogUsageByRestAPI() {
        try {
            HttpResponse response = this.httpClient.execute((HttpUriRequest)new HttpGet(new URL(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + "/").toURI().resolve(AUDIT_CAPACITY_REST_ENDPOINT)));
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200) {
                return Optional.of(ObjectMapperFactory.getObjectMapper().readValue(response.getEntity().getContent(), AuditLogCapacity.class));
            }
            LOG.debug("Invalid response code: {}", (Object)responseCode);
            return Optional.empty();
        }
        catch (Exception e) {
            LOG.debug("Error querying Audit usage via REST API: ", (Throwable)e);
            return Optional.empty();
        }
    }
}

