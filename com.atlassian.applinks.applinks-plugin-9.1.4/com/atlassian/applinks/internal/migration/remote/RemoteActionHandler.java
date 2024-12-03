/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteActionHandler
implements ResponseHandler<Response> {
    private boolean successful;
    private Optional<String> responseBody = Optional.empty();
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteActionHandler.class);

    RemoteActionHandler() {
    }

    public void handle(Response response) throws ResponseException {
        this.successful = response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        this.responseBody = Optional.of(response.getResponseBodyAsString());
        LOGGER.debug("Status: " + response.getStatusCode());
        LOGGER.debug(this.responseBody.get());
    }

    protected boolean isSuccessful() {
        return this.successful;
    }

    public Optional<String> getResponse() {
        return this.responseBody;
    }
}

