/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.core.authentication;

import com.atlassian.oauth2.provider.core.authentication.LogoutException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogoutHandler {
    public void logout(HttpServletRequest var1, HttpServletResponse var2) throws LogoutException;
}

