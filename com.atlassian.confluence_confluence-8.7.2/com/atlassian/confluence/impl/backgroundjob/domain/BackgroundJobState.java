/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob.domain;

public enum BackgroundJobState {
    ACTIVE,
    FAILED,
    CANCELLED,
    FINISHED;


    public static BackgroundJobState fromValue(String enumValue) {
        for (BackgroundJobState state : BackgroundJobState.values()) {
            if (!state.name().equals(enumValue)) continue;
            return state;
        }
        return null;
    }
}

