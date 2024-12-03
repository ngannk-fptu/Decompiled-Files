/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.migration.agent.okhttp;

import java.util.Arrays;
import javax.annotation.Nullable;

enum ErrorCode {
    GENERIC(101),
    UNHANDLED_COMMUNICATION_ERROR_WITH_DOWNSTREAM_SERVER(102),
    FAILED_TO_START_MIGRATION(103),
    BAD_REQUEST(104),
    MIGRATION_NOT_FOUND(105),
    START_MIGRATION_TIMEOUT(106),
    PROGRESS_MIGRATION_TIMEOUT(107);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    @Nullable
    public static ErrorCode getByCode(int code) {
        return Arrays.stream(ErrorCode.values()).filter(v -> v.code == code).findFirst().orElse(null);
    }

    public int getCode() {
        return this.code;
    }
}

