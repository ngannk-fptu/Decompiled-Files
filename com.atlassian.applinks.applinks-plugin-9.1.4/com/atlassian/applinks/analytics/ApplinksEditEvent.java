/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import javax.annotation.Nonnull;

@EventName(value="applinks.edit")
public class ApplinksEditEvent {
    private final String applicationId;
    private final String applicationType;

    private ApplinksEditEvent(Builder builder) {
        this.applicationId = builder.applicationId;
        this.applicationType = builder.applicationType;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public String getApplicationType() {
        return this.applicationType;
    }

    public static class Builder {
        private final String applicationType;
        private final String applicationId;

        public Builder(@Nonnull ApplicationLink applink) {
            this.applicationId = applink.getId().get();
            this.applicationType = ApplicationTypes.resolveApplicationTypeId(applink.getType());
        }

        public ApplinksEditEvent build() {
            return new ApplinksEditEvent(this);
        }
    }
}

