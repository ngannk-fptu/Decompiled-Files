/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.upm.api.util.Option;

public class MpacApplication {
    private final Boolean unknown;
    private final Option<Integer> buildNumber;

    MpacApplication(Boolean unknown, Option<Integer> buildNumber) {
        this.unknown = unknown;
        this.buildNumber = buildNumber;
    }

    public Boolean getUnknown() {
        return this.unknown;
    }

    public Option<Integer> getBuildNumber() {
        return this.buildNumber;
    }
}

