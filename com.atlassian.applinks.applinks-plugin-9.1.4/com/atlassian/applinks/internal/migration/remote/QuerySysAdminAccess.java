/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.internal.migration.remote.RemoteActionHandler;
import com.atlassian.applinks.internal.migration.remote.TryWithAuthentication;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QuerySysAdminAccess
extends TryWithAuthentication {
    public static final TryWithAuthentication INSTANCE = new QuerySysAdminAccess();
    public static final String SYS_ADMIN_ACCESS_PATH = "/plugins/servlet/applinks/auth/conf/trusted/inbound-ual/";
    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySysAdminAccess.class);

    private QuerySysAdminAccess() {
    }

    @Override
    public boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId localApplicationId, @Nonnull ApplicationLinkRequestFactory factory) throws IOException, CredentialsRequiredException, ResponseException {
        String url = SYS_ADMIN_ACCESS_PATH + localApplicationId.toString();
        ApplicationLinkRequest request = factory.createRequest(Request.MethodType.HEAD, url);
        request.setHeader("X-Atlassian-Token", "no-check");
        RemoteActionHandler handler = new RemoteActionHandler();
        request.setConnectionTimeout((int)TimeUnit.SECONDS.toMillis(TryWithAuthentication.TIME_OUT_IN_SECONDS));
        request.setSoTimeout((int)TimeUnit.SECONDS.toMillis(TryWithAuthentication.TIME_OUT_IN_SECONDS));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Request.MethodType.HEAD.name() + " " + applicationLink.getRpcUrl() + url);
        }
        request.execute((ResponseHandler)handler);
        return handler.isSuccessful();
    }
}

