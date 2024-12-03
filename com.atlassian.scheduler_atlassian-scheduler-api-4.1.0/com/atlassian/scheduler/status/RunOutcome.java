/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.scheduler.status;

import com.atlassian.annotations.PublicApi;

@PublicApi
public enum RunOutcome {
    SUCCESS,
    UNAVAILABLE,
    ABORTED,
    FAILED;

}

