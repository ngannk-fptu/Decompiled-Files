/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps.filter;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

@Deprecated
public interface AuthenticationController {
    public boolean shouldAttemptAuthentication(HttpServletRequest var1);

    public boolean canLogin(Principal var1, HttpServletRequest var2);
}

