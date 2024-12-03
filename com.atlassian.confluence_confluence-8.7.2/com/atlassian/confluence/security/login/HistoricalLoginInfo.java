/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.login;

import java.util.Date;

public interface HistoricalLoginInfo {
    public int getCurrentFailedLoginCount();

    public int getTotalFailedLoginCount();

    public Date getLastSuccessfulLoginDate();

    public Date getPreviousSuccessfulLoginDate();

    public Date getLastFailedLoginDate();
}

