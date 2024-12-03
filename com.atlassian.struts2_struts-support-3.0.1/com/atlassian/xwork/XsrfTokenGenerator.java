/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.xwork;

import javax.servlet.http.HttpServletRequest;

public interface XsrfTokenGenerator {
    public String getToken(HttpServletRequest var1, boolean var2);

    public String generateToken(HttpServletRequest var1);

    public String getXsrfTokenName();

    public boolean validateToken(HttpServletRequest var1, String var2);
}

