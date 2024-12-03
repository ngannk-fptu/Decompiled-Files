/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.user.authenticator;

import com.opensymphony.user.authenticator.AuthenticationException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public interface Authenticator {
    public boolean login(String var1, String var2) throws AuthenticationException;

    public boolean init(Properties var1);

    public boolean login(String var1, String var2, HttpServletRequest var3) throws AuthenticationException;
}

