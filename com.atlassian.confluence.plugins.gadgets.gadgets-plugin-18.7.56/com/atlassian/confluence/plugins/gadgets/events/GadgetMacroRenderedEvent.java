/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Ticker
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.gadgets.events.Timer;
import com.google.common.base.Ticker;
import java.net.URI;
import org.joda.time.Duration;

@EventName(value="gadget.macro.rendered")
public class GadgetMacroRenderedEvent {
    private final URI gadgetUri;
    private final boolean ignoreCache;
    private final boolean allowWrites;
    private final String viewMode;
    private final Duration whitelistCheckDuration;
    private final Duration createGadgetViewDuration;
    private final Duration renderGadgetViewDuration;

    private GadgetMacroRenderedEvent(URI gadgetUri, boolean ignoreCache, boolean allowWrites, String viewMode, Duration whitelistCheckDuration, Duration createGadgetViewDuration, Duration renderGadgetViewDuration) {
        this.gadgetUri = gadgetUri;
        this.ignoreCache = ignoreCache;
        this.allowWrites = allowWrites;
        this.viewMode = viewMode;
        this.whitelistCheckDuration = whitelistCheckDuration;
        this.createGadgetViewDuration = createGadgetViewDuration;
        this.renderGadgetViewDuration = renderGadgetViewDuration;
    }

    public URI getGadgetUri() {
        return this.gadgetUri;
    }

    public String getGadgetUriPath() {
        return this.gadgetUri == null ? null : this.gadgetUri.getPath();
    }

    public boolean isIgnoreCache() {
        return this.ignoreCache;
    }

    public boolean isAllowWrites() {
        return this.allowWrites;
    }

    public String getViewMode() {
        return this.viewMode;
    }

    public long getWhitelistCheckMillis() {
        return this.whitelistCheckDuration.getMillis();
    }

    public long getCreateGadgetViewMillis() {
        return this.createGadgetViewDuration.getMillis();
    }

    public long getRenderGadgetViewMillis() {
        return this.renderGadgetViewDuration.getMillis();
    }

    public static Builder builder() {
        return new Builder(Ticker.systemTicker());
    }

    public static class Builder {
        private final Timer whitelistCheckTimer;
        private final Timer createGadgetViewTimer;
        private final Timer renderGadgetViewTimer;
        private URI gadgetUri;
        private boolean ignoreCache;
        private boolean allowWrites;
        private String viewMode;

        Builder(Ticker ticker) {
            this.whitelistCheckTimer = new Timer(ticker);
            this.createGadgetViewTimer = new Timer(ticker);
            this.renderGadgetViewTimer = new Timer(ticker);
        }

        public Builder withGadgetUri(URI gadgetUri) {
            this.gadgetUri = gadgetUri;
            return this;
        }

        public GadgetMacroRenderedEvent build() {
            return new GadgetMacroRenderedEvent(this.gadgetUri, this.ignoreCache, this.allowWrites, this.viewMode, this.whitelistCheckTimer.duration(), this.createGadgetViewTimer.duration(), this.renderGadgetViewTimer.duration());
        }

        public void gadgetWhitelistCheckStart() {
            this.whitelistCheckTimer.start();
        }

        public void gadgetWhitelistCheckFinish() {
            this.whitelistCheckTimer.stop();
        }

        public void withIgnoreCache(boolean ignoreCache) {
            this.ignoreCache = ignoreCache;
        }

        public void withAllowWrites(boolean allowWrites) {
            this.allowWrites = allowWrites;
        }

        public void withViewMode(String viewMode) {
            this.viewMode = viewMode;
        }

        public void createGadgetViewStart() {
            this.createGadgetViewTimer.start();
        }

        public void createGadgetViewFinish() {
            this.createGadgetViewTimer.stop();
        }

        public void renderGadgetViewStart() {
            this.renderGadgetViewTimer.start();
        }

        public void renderGadgetViewFinish() {
            this.renderGadgetViewTimer.stop();
        }
    }
}

