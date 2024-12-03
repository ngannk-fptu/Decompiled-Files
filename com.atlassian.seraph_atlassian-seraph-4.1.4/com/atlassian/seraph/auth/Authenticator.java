/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.Initable;
import com.atlassian.seraph.auth.AuthenticatorException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator
extends Initable {
    public void destroy();

    public String getRemoteUser(HttpServletRequest var1);

    public Principal getUser(HttpServletRequest var1);

    public Principal getUser(HttpServletRequest var1, HttpServletResponse var2);

    public boolean login(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4) throws AuthenticatorException;

    public boolean login(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4, boolean var5) throws AuthenticatorException;

    public boolean logout(HttpServletRequest var1, HttpServletResponse var2) throws AuthenticatorException;
}

