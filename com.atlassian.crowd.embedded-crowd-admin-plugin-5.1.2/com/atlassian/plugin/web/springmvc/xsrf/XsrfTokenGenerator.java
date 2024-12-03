/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugin.web.springmvc.xsrf;

import javax.servlet.http.HttpServletRequest;

public interface XsrfTokenGenerator {
    public static final String REQUEST_PARAM_NAME = "atl_token";

    public String generateToken(HttpServletRequest var1);

    public String getXsrfTokenName();

    public boolean validateToken(HttpServletRequest var1, String var2);
}

