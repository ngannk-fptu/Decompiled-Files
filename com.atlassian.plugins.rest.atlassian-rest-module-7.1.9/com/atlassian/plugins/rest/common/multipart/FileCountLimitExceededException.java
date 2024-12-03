/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FileCountLimitExceededException
extends WebApplicationException {
    private static final int PAYLOAD_TOO_LARGE = 413;

    private static int getStatusCode() {
        return 413;
    }

    public FileCountLimitExceededException(String message) {
        super(Response.status(FileCountLimitExceededException.getStatusCode()).entity(message).build());
    }
}

