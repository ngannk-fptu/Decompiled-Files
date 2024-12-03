/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(RestHelper.class);

    public String executeRest(String restUrl, ReadOnlyApplicationLink appLink) {
        String json = "";
        try {
            ApplicationLinkRequest fieldRequest = JiraConnectorUtils.getApplicationLinkRequest(appLink, Request.MethodType.GET, restUrl);
            fieldRequest.addHeader("Content-Type", "application/json");
            json = fieldRequest.execute();
        }
        catch (CredentialsRequiredException e) {
            LOGGER.error("CredentialsRequiredException", (Throwable)e);
        }
        catch (ResponseException e) {
            LOGGER.error("ResponseExceptionException", (Throwable)e);
        }
        return json;
    }
}

