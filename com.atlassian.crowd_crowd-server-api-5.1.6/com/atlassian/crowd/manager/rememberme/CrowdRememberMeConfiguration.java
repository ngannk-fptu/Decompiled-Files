/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.manager.rememberme;

import com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings;
import javax.servlet.http.HttpServletRequest;

public interface CrowdRememberMeConfiguration {
    public String getCookieName();

    public long getCookieMaxAgeInMillis();

    public long getGracePeriodInMillis();

    public String getCookiePath(HttpServletRequest var1);

    public boolean isCookieHttpOnly(HttpServletRequest var1);

    public void saveConfiguration(CrowdSpecificRememberMeSettings var1);

    public CrowdSpecificRememberMeSettings getConfiguration();
}

