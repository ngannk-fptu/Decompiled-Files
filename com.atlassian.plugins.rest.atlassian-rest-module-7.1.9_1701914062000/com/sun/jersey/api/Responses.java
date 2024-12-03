/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api;

import javax.ws.rs.core.Response;

public class Responses {
    public static final int NO_CONTENT = 204;
    public static final int NOT_MODIFIED = 304;
    public static final int CLIENT_ERROR = 400;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int CONFLICT = 409;
    public static final int PRECONDITION_FAILED = 412;
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    private static Response.StatusType METHOD_NOT_ALLOWED_TYPE = new Response.StatusType(){

        @Override
        public int getStatusCode() {
            return 405;
        }

        @Override
        public Response.Status.Family getFamily() {
            return Response.Status.Family.CLIENT_ERROR;
        }

        @Override
        public String getReasonPhrase() {
            return "Method Not Allowed";
        }
    };

    public static Response.ResponseBuilder noContent() {
        return Responses.status(Response.Status.NO_CONTENT);
    }

    public static Response.ResponseBuilder notModified() {
        return Responses.status(Response.Status.NOT_MODIFIED);
    }

    public static Response.ResponseBuilder clientError() {
        return Responses.status(Response.Status.BAD_REQUEST);
    }

    public static Response.ResponseBuilder notFound() {
        return Responses.status(Response.Status.NOT_FOUND);
    }

    public static Response.ResponseBuilder methodNotAllowed() {
        return Responses.status(METHOD_NOT_ALLOWED_TYPE);
    }

    public static Response.ResponseBuilder notAcceptable() {
        return Responses.status(Response.Status.NOT_ACCEPTABLE);
    }

    public static Response.ResponseBuilder conflict() {
        return Responses.status(Response.Status.CONFLICT);
    }

    public static Response.ResponseBuilder preconditionFailed() {
        return Responses.status(Response.Status.PRECONDITION_FAILED);
    }

    public static Response.ResponseBuilder unsupportedMediaType() {
        return Responses.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }

    private static Response.ResponseBuilder status(Response.StatusType status) {
        return Response.status(status);
    }
}

