/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.sal.api.net.Request;

public interface ApplicationLinkRequestFactory
extends AuthorisationURIGenerator {
    public ApplicationLinkRequest createRequest(Request.MethodType var1, String var2) throws CredentialsRequiredException;
}

