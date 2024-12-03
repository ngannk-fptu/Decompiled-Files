/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.api.auth;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public interface AuthenticationController {
    public boolean shouldAttemptAuthentication(HttpServletRequest var1);

    public boolean canLogin(Principal var1, HttpServletRequest var2);
}

