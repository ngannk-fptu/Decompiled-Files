/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

public class DisallowStartupInCloud {
    public static final String UPM_ON_DEMAND = "atlassian.upm.on.demand";

    public DisallowStartupInCloud() {
        if (Boolean.getBoolean(UPM_ON_DEMAND)) {
            throw new IllegalStateException("this Server version of UPM cannot be used in a Cloud environment");
        }
    }
}

