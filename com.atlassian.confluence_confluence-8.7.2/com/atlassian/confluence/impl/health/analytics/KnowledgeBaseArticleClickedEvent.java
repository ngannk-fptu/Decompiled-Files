/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEvent;
import java.util.Objects;

@ParametersAreNonnullByDefault
@EventName(value="johnson.knowledge.base.article.clicked.in.suppressed.error.page")
public class KnowledgeBaseArticleClickedEvent {
    private final String kbURL;

    KnowledgeBaseArticleClickedEvent(String kbURL) {
        this.kbURL = Objects.requireNonNull(kbURL);
    }

    public String getKbURL() {
        return HealthCheckAnalyticsEvent.sanitise(this.kbURL);
    }
}

