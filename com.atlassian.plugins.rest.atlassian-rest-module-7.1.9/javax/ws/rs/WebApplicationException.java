/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs;

import javax.ws.rs.core.Response;

public class WebApplicationException
extends RuntimeException {
    private static final long serialVersionUID = 11660101L;
    private Response response;

    public WebApplicationException() {
        this(null, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public WebApplicationException(Response response) {
        this(null, response);
    }

    public WebApplicationException(int status) {
        this(null, status);
    }

    public WebApplicationException(Response.Status status) {
        this(null, status);
    }

    public WebApplicationException(Throwable cause) {
        this(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public WebApplicationException(Throwable cause, Response response) {
        super(cause);
        this.response = response == null ? Response.serverError().build() : response;
    }

    public WebApplicationException(Throwable cause, int status) {
        this(cause, Response.status(status).build());
    }

    public WebApplicationException(Throwable cause, Response.Status status) {
        this(cause, Response.status(status).build());
    }

    public Response getResponse() {
        return this.response;
    }
}

