/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.usercontext;

import java.security.Principal;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface PrincipalResolver {
    public Optional<Principal> resolvePrincipal(String var1, HttpServletRequest var2);

    public boolean isAllowedToAuthenticate(Principal var1, HttpServletRequest var2);
}

