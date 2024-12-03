/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnsupportedFileNameEncodingException
extends WebApplicationException {
    public UnsupportedFileNameEncodingException(String rawFileName) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(String.format("The encoding of file name '%s' is invalid according to RFC 2047", rawFileName)).build());
    }
}

