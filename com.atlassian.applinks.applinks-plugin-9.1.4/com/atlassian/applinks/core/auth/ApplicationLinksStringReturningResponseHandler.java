/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.atlassian.sal.api.net.ReturningResponseHandler;

public class ApplicationLinksStringReturningResponseHandler
implements ReturningResponseHandler<Response, String> {
    public String handle(Response response) throws ResponseException {
        if (!response.isSuccessful()) {
            throw new ResponseStatusException("Unexpected response received. Status code: " + response.getStatusCode(), response);
        }
        return response.getResponseBodyAsString();
    }
}

