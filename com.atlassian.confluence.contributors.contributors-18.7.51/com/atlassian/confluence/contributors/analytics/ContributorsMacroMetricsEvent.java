/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.base.Ticker
 *  com.google.common.collect.Sets
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.contributors.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.contributors.analytics.Timer;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.base.Ticker;
import com.google.common.collect.Sets;
import java.util.Collection;
import org.joda.time.Duration;

@EventName(value="confluence.macro.metrics.contributors")
@AsynchronousPreferred
public class ContributorsMacroMetricsEvent {
    private final Duration documentFetchTime;
    private final int documentCount;
    private final Duration userFetchTime;
    private final int userCount;
    private final Duration templateRenderTime;
    private final Duration buildModelTime;
    private final MacroParameterModel.LayoutStyle layoutStyle;
    private final Collection<MacroParameterModel.ContributorsMacroInclude> includes;

    public ContributorsMacroMetricsEvent(Duration documentFetchTime, int documentCount, Duration userFetchTime, int userCount, Duration templateRenderTime, Duration buildModelTime, MacroParameterModel.LayoutStyle layoutStyle, Collection<MacroParameterModel.ContributorsMacroInclude> includes) {
        this.documentFetchTime = documentFetchTime;
        this.documentCount = documentCount;
        this.userFetchTime = userFetchTime;
        this.userCount = userCount;
        this.templateRenderTime = templateRenderTime;
        this.buildModelTime = buildModelTime;
        this.layoutStyle = layoutStyle;
        this.includes = includes;
    }

    public long getDocumentFetchTimeMillis() {
        return this.documentFetchTime.getMillis();
    }

    public int getDocumentCount() {
        return this.documentCount;
    }

    public long getUserFetchTimeMillis() {
        return this.userFetchTime.getMillis();
    }

    public int getUserCount() {
        return this.userCount;
    }

    public long getTemplateRenderTimeMillis() {
        return this.templateRenderTime.getMillis();
    }

    public long getBuildModelTimeMillis() {
        return this.buildModelTime.getMillis();
    }

    public String getLayoutStyle() {
        return this.layoutStyle != null ? this.layoutStyle.toString() : "";
    }

    public boolean isIncludeAuthors() {
        return this.includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.AUTHORS);
    }

    public boolean isIncludeWatches() {
        return this.includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.WATCHES);
    }

    public boolean isIncludeLabels() {
        return this.includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.LABELS);
    }

    public boolean isIncludeComments() {
        return this.includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.COMMENTS);
    }

    public static Builder builder() {
        return new Builder(Ticker.systemTicker());
    }

    public static class Builder {
        private final Timer documentFetchTimer;
        private int documentCount;
        private final Timer userFetchTimer;
        private int userCount;
        private MacroParameterModel.LayoutStyle layoutStyle;
        private final Timer templateRenderTimer;
        private final Timer buildModelTimer;
        private final Collection<MacroParameterModel.ContributorsMacroInclude> includes = Sets.newHashSet();

        public Builder(Ticker ticker) {
            this.documentFetchTimer = new Timer(ticker);
            this.userFetchTimer = new Timer(ticker);
            this.templateRenderTimer = new Timer(ticker);
            this.buildModelTimer = new Timer(ticker);
        }

        public Builder documentFetchStart() {
            this.documentFetchTimer.start();
            return this;
        }

        public Builder documentFetchFinish(int documentCount) {
            this.documentFetchTimer.stop();
            this.documentCount = documentCount;
            return this;
        }

        public Builder userFetchStart(Collection<MacroParameterModel.ContributorsMacroInclude> includes) {
            this.includes.addAll(includes);
            this.userFetchTimer.start();
            return this;
        }

        public Builder userFetchFinish(int userCount) {
            this.userFetchTimer.stop();
            this.userCount = userCount;
            return this;
        }

        public Builder templateRenderStart() {
            this.templateRenderTimer.start();
            return this;
        }

        public Builder templateRenderFinish() {
            this.templateRenderTimer.stop();
            return this;
        }

        public ContributorsMacroMetricsEvent build() {
            return new ContributorsMacroMetricsEvent(this.documentFetchTimer.duration(), this.documentCount, this.userFetchTimer.duration(), this.userCount, this.templateRenderTimer.duration(), this.buildModelTimer.duration(), this.layoutStyle, this.includes);
        }

        public void buildTemplateModelStart() {
            this.buildModelTimer.start();
        }

        public void buildTemplateModelFinish(MacroParameterModel.LayoutStyle layoutStyle) {
            this.layoutStyle = layoutStyle;
            this.buildModelTimer.stop();
        }
    }
}

