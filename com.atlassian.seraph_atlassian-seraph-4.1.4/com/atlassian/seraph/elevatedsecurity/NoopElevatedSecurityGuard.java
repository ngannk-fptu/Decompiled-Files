/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.elevatedsecurity;

import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class NoopElevatedSecurityGuard
implements ElevatedSecurityGuard {
    public static final NoopElevatedSecurityGuard INSTANCE = new NoopElevatedSecurityGuard();

    private NoopElevatedSecurityGuard() {
    }

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
    }

    @Override
    public boolean performElevatedSecurityCheck(HttpServletRequest httpServletRequest, String userName) {
        return true;
    }

    @Override
    public void onFailedLoginAttempt(HttpServletRequest httpServletRequest, String userName) {
    }

    @Override
    public void onSuccessfulLoginAttempt(HttpServletRequest httpServletRequest, String userName) {
    }
}

