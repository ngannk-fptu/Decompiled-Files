/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.entities.UserHistoryList;
import com.atlassian.confluence.plugins.rest.entities.UserSessionEntity;

public interface RestUserSessionManager {
    public UserSessionEntity getUserSession();

    public UserHistoryList getUserHistory(Integer var1, Integer var2);
}

