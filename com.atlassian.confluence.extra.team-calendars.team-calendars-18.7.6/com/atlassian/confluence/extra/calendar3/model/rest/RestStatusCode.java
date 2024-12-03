/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.extra.calendar3.model.rest;

import javax.ws.rs.core.Response;

public final class RestStatusCode {
    public static final int OK = Response.Status.OK.getStatusCode();
    public static final int ERROR = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
}

