/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class ApplinksCreatedEventFactory {
    private final ApplicationProperties applicationProperties;
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public ApplinksCreatedEventFactory(ApplicationProperties applicationProperties, InternalHostApplication internalHostApplication) {
        this.applicationProperties = applicationProperties;
        this.internalHostApplication = internalHostApplication;
    }

    public ApplinksCreatedEvent createFailEvent(FAILURE_REASON reason) {
        return this.createEvent(EVENT_STATUS.FAILURE, reason.name());
    }

    public ApplinksCreatedEvent createWarningEvent(FAILURE_REASON reason) {
        return this.createEvent(EVENT_STATUS.WARNING, reason.name());
    }

    public ApplinksCreatedEvent createSuccessEvent() {
        return this.createEvent(EVENT_STATUS.SUCCESS, null);
    }

    private ApplinksCreatedEvent createEvent(EVENT_STATUS status, @Nullable String reasonName) {
        return new ApplinksCreatedEvent(this.applicationProperties.getPlatformId(), this.internalHostApplication.getId().get(), reasonName, status.name());
    }

    @EventName(value="applinks.created")
    @ParametersAreNonnullByDefault
    static class ApplinksCreatedEvent {
        @Nonnull
        private final String applicationId;
        @Nonnull
        private final String product;
        @Nullable
        private final String reason;
        @Nonnull
        private final String status;

        public ApplinksCreatedEvent(String product, String applicationId, @Nullable String reason, String status) {
            this.applicationId = Objects.requireNonNull(applicationId, "applicationId can't be null");
            this.product = Objects.requireNonNull(product, "product can't be null");
            this.reason = reason;
            this.status = Objects.requireNonNull(status, "status can't be null");
        }

        @Nonnull
        public String getApplicationId() {
            return this.applicationId;
        }

        @Nonnull
        public String getProduct() {
            return this.product;
        }

        @Nullable
        public String getReason() {
            return this.reason;
        }

        @Nonnull
        public String getStatus() {
            return this.status;
        }
    }

    public static enum FAILURE_REASON {
        ALREADY_CONFIGURED,
        ALREADY_CONFIGURED_UNDER_DIFFERENT_URL,
        INVALID_URL,
        LINK_TO_SELF,
        NO_DOUBLE_SLASHES,
        NO_RESPONSE,
        NULL_MANIFEST,
        REDIRECT,
        TYPE_NOT_INSTALLED;

    }

    public static enum EVENT_STATUS {
        FAILURE,
        WARNING,
        SUCCESS;

    }
}

