/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  javax.ws.rs.core.Response$StatusType
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.header.OutBoundHeaders;
import java.lang.reflect.Type;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class ResponseImpl
extends Response {
    private final Response.StatusType statusType;
    private final MultivaluedMap<String, Object> headers;
    private final Object entity;
    private final Type entityType;

    protected ResponseImpl(Response.StatusType statusType, OutBoundHeaders headers, Object entity, Type entityType) {
        this.statusType = statusType;
        this.headers = headers;
        this.entity = entity;
        this.entityType = entityType;
    }

    protected ResponseImpl(int status, OutBoundHeaders headers, Object entity, Type entityType) {
        this.statusType = ResponseImpl.toStatusType(status);
        this.headers = headers;
        this.entity = entity;
        this.entityType = entityType;
    }

    public Response.StatusType getStatusType() {
        return this.statusType;
    }

    public Type getEntityType() {
        return this.entityType;
    }

    public int getStatus() {
        return this.statusType.getStatusCode();
    }

    public MultivaluedMap<String, Object> getMetadata() {
        return this.headers;
    }

    public Object getEntity() {
        return this.entity;
    }

    public static Response.StatusType toStatusType(final int statusCode) {
        switch (statusCode) {
            case 200: {
                return Response.Status.OK;
            }
            case 201: {
                return Response.Status.CREATED;
            }
            case 202: {
                return Response.Status.ACCEPTED;
            }
            case 204: {
                return Response.Status.NO_CONTENT;
            }
            case 301: {
                return Response.Status.MOVED_PERMANENTLY;
            }
            case 303: {
                return Response.Status.SEE_OTHER;
            }
            case 304: {
                return Response.Status.NOT_MODIFIED;
            }
            case 307: {
                return Response.Status.TEMPORARY_REDIRECT;
            }
            case 400: {
                return Response.Status.BAD_REQUEST;
            }
            case 401: {
                return Response.Status.UNAUTHORIZED;
            }
            case 403: {
                return Response.Status.FORBIDDEN;
            }
            case 404: {
                return Response.Status.NOT_FOUND;
            }
            case 406: {
                return Response.Status.NOT_ACCEPTABLE;
            }
            case 409: {
                return Response.Status.CONFLICT;
            }
            case 410: {
                return Response.Status.GONE;
            }
            case 412: {
                return Response.Status.PRECONDITION_FAILED;
            }
            case 415: {
                return Response.Status.UNSUPPORTED_MEDIA_TYPE;
            }
            case 500: {
                return Response.Status.INTERNAL_SERVER_ERROR;
            }
            case 503: {
                return Response.Status.SERVICE_UNAVAILABLE;
            }
        }
        return new Response.StatusType(){

            public int getStatusCode() {
                return statusCode;
            }

            public Response.Status.Family getFamily() {
                return ResponseImpl.toFamilyCode(statusCode);
            }

            public String getReasonPhrase() {
                return "";
            }
        };
    }

    public static Response.Status.Family toFamilyCode(int statusCode) {
        switch (statusCode / 100) {
            case 1: {
                return Response.Status.Family.INFORMATIONAL;
            }
            case 2: {
                return Response.Status.Family.SUCCESSFUL;
            }
            case 3: {
                return Response.Status.Family.REDIRECTION;
            }
            case 4: {
                return Response.Status.Family.CLIENT_ERROR;
            }
            case 5: {
                return Response.Status.Family.SERVER_ERROR;
            }
        }
        return Response.Status.Family.OTHER;
    }
}

