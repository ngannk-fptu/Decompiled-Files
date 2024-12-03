/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.core.rest.resources.permission;

import java.util.Objects;
import javax.ws.rs.core.Response;

public class PermissionException
extends RuntimeException {
    private final Response.Status status;
    private final String message;

    private PermissionException(Response.Status status, String message) {
        this.status = Objects.requireNonNull(status, "status");
        this.message = Objects.requireNonNull(message, "message");
    }

    public static PermissionException conflict() {
        return new PermissionException(Response.Status.CONFLICT, "Cannot access this resource because a precondition failed.");
    }

    public static PermissionException forbidden() {
        return new PermissionException(Response.Status.FORBIDDEN, "Cannot access this resource on this system.");
    }

    public static PermissionException unauthorized() {
        return new PermissionException(Response.Status.UNAUTHORIZED, "Must have permission to access this resource.");
    }

    public Response.Status getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

