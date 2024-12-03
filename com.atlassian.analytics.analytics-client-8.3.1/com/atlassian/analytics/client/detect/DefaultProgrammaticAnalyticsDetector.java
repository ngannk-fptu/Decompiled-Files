/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.detect;

import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;

public class DefaultProgrammaticAnalyticsDetector
implements ProgrammaticAnalyticsDetector {
    private final boolean isEnabled;

    public DefaultProgrammaticAnalyticsDetector() {
        this(false);
    }

    public DefaultProgrammaticAnalyticsDetector(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}

