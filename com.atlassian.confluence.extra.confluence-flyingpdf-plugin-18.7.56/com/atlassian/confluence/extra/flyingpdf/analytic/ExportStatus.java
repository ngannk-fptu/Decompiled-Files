/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import java.util.EnumSet;

public enum ExportStatus {
    OK,
    FAIL,
    SANDBOX_CRASH,
    SANDBOX_TIMEOUT;

    private static EnumSet<ExportStatus> failedStatuses;

    public static boolean isFail(ExportStatus status) {
        return failedStatuses.contains((Object)status);
    }

    public static boolean isSuccessful(ExportStatus status) {
        return status != null && !ExportStatus.isFail(status);
    }

    static {
        failedStatuses = EnumSet.of(FAIL, SANDBOX_CRASH, SANDBOX_TIMEOUT);
    }
}

