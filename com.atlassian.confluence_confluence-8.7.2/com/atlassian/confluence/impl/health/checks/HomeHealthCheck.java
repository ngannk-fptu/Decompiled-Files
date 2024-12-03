/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.johnson.event.Event
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.HomeHealthCheckFailure;
import com.atlassian.confluence.impl.health.checks.HomeHealthCheckMessageFactory;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class HomeHealthCheck
extends HealthCheckTemplate {
    private static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/display/CONFKB/Startup+check%3A+Setting+your+Confluence+home?utm_source=Install&utm_medium=in-product&utm_campaign=csseng_fy18_q3_server_confluence_errorstate");
    private final HomeHealthCheckMessageFactory homeHealthCheckMessageFactory = new HomeHealthCheckMessageFactory();

    public HomeHealthCheck() {
        super(Collections.emptyList());
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return ImmutableSet.of((Object)((Object)LifecyclePhase.SETUP));
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        return this.checkHomeConfigured().map(f -> Collections.singletonList(this.toHealthCheckResult((HomeHealthCheckFailure)f))).orElse(Collections.emptyList());
    }

    private Optional<HomeHealthCheckFailure> checkHomeConfigured() {
        String proposedHome = this.getProposedHome();
        if (StringUtils.isBlank((CharSequence)proposedHome)) {
            return Optional.of(HomeHealthCheckFailure.missingConfiguration(HomeHealthCheckFailure.Reason.NOT_CONFIGURED));
        }
        File homeDir = new File(proposedHome);
        if (homeDir.exists()) {
            if (!homeDir.isDirectory()) {
                return Optional.of(HomeHealthCheckFailure.badConfiguredHome(HomeHealthCheckFailure.Reason.NOT_A_DIR, proposedHome));
            }
            if (!homeDir.canWrite()) {
                return Optional.of(HomeHealthCheckFailure.badConfiguredHome(HomeHealthCheckFailure.Reason.CREATION_FAILED_WRITE_PERMISSION, proposedHome));
            }
        }
        return Optional.empty();
    }

    private String getProposedHome() {
        return BootstrapUtils.getBootstrapManager().getConfiguredApplicationHome();
    }

    private HealthCheckResult toHealthCheckResult(HomeHealthCheckFailure healthCheckFailure) {
        HealthCheckMessage message = this.homeHealthCheckMessageFactory.getMessage(healthCheckFailure);
        Event event = new Event(JohnsonEventType.SETUP.eventType(), message.getHeadline(), message.asHtml(), JohnsonEventLevel.FATAL.level());
        return HealthCheckResult.fail(this, event, KB_URL, healthCheckFailure.getReason().getAnalyticsValue(), message.asText()).get(0);
    }
}

