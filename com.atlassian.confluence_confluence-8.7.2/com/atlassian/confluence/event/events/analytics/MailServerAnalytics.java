/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

public class MailServerAnalytics {
    private MailServerAnalytics() {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.mail.outbound-server.deleted")
    public static class OutboundServerDeleted
    extends AbstractMailAnalytics {
        public OutboundServerDeleted(String protocol) {
            super(protocol);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.mail.outbound-server.created")
    public static class OutboundServerCreated
    extends AbstractMailAnalytics {
        public OutboundServerCreated(String protocol) {
            super(protocol);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.mail.inbound-server.deleted")
    public static class InboundServerDeleted
    extends AbstractMailAnalytics {
        public InboundServerDeleted(String protocol) {
            super(protocol);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.mail.inbound-server.created")
    public static class InboundServerCreated
    extends AbstractMailAnalytics {
        public InboundServerCreated(String protocol) {
            super(protocol);
        }
    }

    private static abstract class AbstractMailAnalytics {
        private final String protocol;

        protected AbstractMailAnalytics(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocol() {
            return this.protocol;
        }
    }
}

