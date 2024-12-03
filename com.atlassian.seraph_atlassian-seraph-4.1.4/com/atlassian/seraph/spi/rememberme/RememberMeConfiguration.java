/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.spi.rememberme;

import javax.servlet.http.HttpServletRequest;

public interface RememberMeConfiguration {
    public String getCookieName();

    public int getCookieMaxAgeInSeconds();

    public String getCookieDomain(HttpServletRequest var1);

    public String getCookiePath(HttpServletRequest var1);

    public boolean isInsecureCookieAlwaysUsed();

    public boolean isCookieHttpOnly(HttpServletRequest var1);
}

