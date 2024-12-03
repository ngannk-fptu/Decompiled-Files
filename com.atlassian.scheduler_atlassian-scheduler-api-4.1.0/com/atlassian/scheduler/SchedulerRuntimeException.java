/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class SchedulerRuntimeException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SchedulerRuntimeException(String message) {
        super(message);
    }

    public SchedulerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

