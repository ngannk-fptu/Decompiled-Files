/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.search.timeout")
public class SearchTimeoutEvent
extends BaseAnalyticEvent {
    private final boolean freetext;
    private final boolean limited;

    public SearchTimeoutEvent(boolean freetext, boolean limited, String pluginVersion) {
        super(pluginVersion);
        this.freetext = freetext;
        this.limited = limited;
    }

    public boolean isFreetext() {
        return this.freetext;
    }

    public boolean isLimited() {
        return this.limited;
    }
}

