/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuthProblemException
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuthProblemException;

public interface TokenLoader {
    public ServiceProviderToken getTokenForAuthorization(HttpServletRequest var1) throws OAuthProblemException, IOException;
}

