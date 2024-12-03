/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.api.xsrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface XsrfTokenAccessor {
    public String getXsrfToken(HttpServletRequest var1, HttpServletResponse var2, boolean var3);
}

