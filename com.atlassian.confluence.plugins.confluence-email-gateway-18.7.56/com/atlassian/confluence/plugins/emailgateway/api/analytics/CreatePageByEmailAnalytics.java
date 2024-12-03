/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.emailgateway.api.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

public class CreatePageByEmailAnalytics {
    private CreatePageByEmailAnalytics() {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.email-to-page.create.page")
    public static class CreatePage {
        private final String spaceKey;
        private final long contentID;

        public CreatePage(String spaceKey, long contentID) {
            this.spaceKey = spaceKey;
            this.contentID = contentID;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public long getContentID() {
            return this.contentID;
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.email-to-page.reject.page")
    public static class RejectPageCreate {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.email-to-page.disable.feature")
    public static class DisableFeatureEvent {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.email-to-page.enable.feature")
    public static class EnableFeatureEvent {
    }
}

