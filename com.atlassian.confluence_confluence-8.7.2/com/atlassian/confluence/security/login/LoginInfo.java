/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.login;

import com.atlassian.confluence.security.login.HistoricalLoginInfo;

public interface LoginInfo
extends HistoricalLoginInfo {
    public boolean requiresElevatedSecurityCheck();
}

