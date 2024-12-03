/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.filter.Authenticator$Result
 *  com.atlassian.security.auth.trustedapps.filter.Authenticator$Result$Success
 *  com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.security.trust.seraph;

import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.security.auth.trustedapps.filter.Authenticator;
import com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationListener;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfluenceSeraphAuthenticationListener
extends SeraphAuthenticationListener {
    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
        ConfluenceUserPrincipal principal = ConfluenceUserPrincipal.of(result.getUser());
        Authenticator.Result.Success success = (Authenticator.Result.Success)result;
        super.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success((Principal)principal, success.getSignedUrl()), request, response);
    }
}

