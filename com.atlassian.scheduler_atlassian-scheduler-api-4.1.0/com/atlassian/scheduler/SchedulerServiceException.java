/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class SchedulerServiceException
extends Exception {
    private static final long serialVersionUID = 1L;

    public SchedulerServiceException(String message) {
        super(message);
    }

    public SchedulerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

