/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob;

import com.atlassian.confluence.impl.backgroundjob.domain.BackgroundJobState;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

public class BackgroundJobResponse {
    private final BackgroundJobState newState;
    private final Instant nextRunAt;
    private final Map<String, Object> newParameters;
    private final String errorMessage;

    private BackgroundJobResponse(BackgroundJobState newState, Instant nextRunAt, Map<String, Object> newParameters, String errorMessage) {
        this.newState = newState;
        this.nextRunAt = nextRunAt;
        this.newParameters = newParameters;
        this.errorMessage = errorMessage;
    }

    public static BackgroundJobResponse scheduleNextRun(ChronoUnit unit, int duration, Map<String, Object> parameters) {
        Instant nextRunAt = Instant.now().plus((long)duration, unit);
        return new BackgroundJobResponse(BackgroundJobState.ACTIVE, nextRunAt, parameters, "");
    }

    public static BackgroundJobResponse scheduleNextRunNow(Map<String, Object> parameters) {
        Instant nextRunAt = Instant.now();
        return new BackgroundJobResponse(BackgroundJobState.ACTIVE, nextRunAt, parameters, "");
    }

    public static BackgroundJobResponse markJobAsFinished() {
        return new BackgroundJobResponse(BackgroundJobState.FINISHED, Instant.now(), Collections.emptyMap(), "");
    }

    public BackgroundJobState getNewState() {
        return this.newState;
    }

    public Instant getNextRunAt() {
        return this.nextRunAt;
    }

    public Map<String, Object> getNewParameters() {
        return this.newParameters;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

