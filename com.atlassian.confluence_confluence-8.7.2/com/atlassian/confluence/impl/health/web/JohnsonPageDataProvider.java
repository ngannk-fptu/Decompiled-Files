/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.config.JohnsonConfig
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.health.HealthCheckRunner;
import com.atlassian.confluence.impl.health.web.JohnsonEventCollectionSerializer;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.seraph.config.SecurityConfig;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class JohnsonPageDataProvider {
    @VisibleForTesting
    static final String HIDE_EVENT_DETAILS = "hide.system.error.details";
    private final HealthCheckRunner healthCheckRunner;
    private final JohnsonConfig johnsonConfig;
    private final JohnsonEventCollectionSerializer eventCollectionSerializer;
    private final JohnsonEventContainer johnsonContainer;
    private final SecurityConfig securityConfig;

    public JohnsonPageDataProvider(HealthCheckRunner healthCheckRunner, JohnsonConfig johnsonConfig, JohnsonEventCollectionSerializer eventCollectionSerializer, JohnsonEventContainer johnsonContainer, SecurityConfig securityConfig) {
        this.healthCheckRunner = Objects.requireNonNull(healthCheckRunner);
        this.eventCollectionSerializer = Objects.requireNonNull(eventCollectionSerializer);
        this.johnsonConfig = Objects.requireNonNull(johnsonConfig);
        this.johnsonContainer = Objects.requireNonNull(johnsonContainer);
        this.securityConfig = Objects.requireNonNull(securityConfig);
    }

    public @NonNull Json getPageData() {
        JsonObject json = new JsonObject().setProperty("canAuthoriseUsers", this.canAuthoriseUsers()).setProperty("checksComplete", this.healthCheckRunner.isComplete());
        if (StringUtils.isBlank((CharSequence)System.getProperty(HIDE_EVENT_DETAILS))) {
            Collection events = this.johnsonContainer.getEvents();
            json.setProperty("events", this.eventCollectionSerializer.toJson(events));
        } else if (this.startupBlocked()) {
            json.setProperty("errorsPresentButHidden", true);
        }
        return json;
    }

    private boolean canAuthoriseUsers() {
        return this.johnsonConfig.getSetupConfig().isSetup() && this.securityConfig.getAuthenticator() != null;
    }

    private boolean startupBlocked() {
        return JohnsonUtils.eventExists(this.johnsonContainer, JohnsonEventPredicates.blocksStartup());
    }
}

