/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Response;

public class UnexpectedResponseException
extends RuntimeException {
    private Response response;

    public UnexpectedResponseException(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected response '").append(this.response.getStatusCode()).append("' with message '");
        sb.append(this.response.getStatusText()).append("'");
        return sb.toString();
    }
}

