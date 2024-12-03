/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import javax.ws.rs.core.Response;

public class InlineCommentResult<T> {
    private final Status status;
    private final T value;
    private final String errorMessage;

    public InlineCommentResult(Status status) {
        this(status, null);
    }

    public InlineCommentResult(Status status, T value) {
        this(status, value, null);
    }

    public InlineCommentResult(Status status, T value, String errorMessage) {
        this.status = status;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return this.status;
    }

    public T getValue() {
        return this.value;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Response buildResponse() {
        switch (this.status) {
            case SUCCESS: {
                return Response.ok(this.value).build();
            }
            case NOT_PERMITTED: {
                return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)Response.Status.UNAUTHORIZED.getReasonPhrase()).build();
            }
            case REQUEST_DATA_INCORRECT: {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)Response.Status.BAD_REQUEST.getReasonPhrase()).build();
            }
            case BAD_REQUEST_UTF8_MYSQL_ERROR: {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.getErrorMessage()).build();
            }
            case NOT_FOUND: {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)Response.Status.NOT_FOUND.getReasonPhrase()).build();
            }
            case DELETE_FAILED: {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity(this.value).build();
            }
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase()).build();
    }

    public static <T> InlineCommentResult<T> getResultFromServiceException(Exception e) {
        if (e instanceof PermissionException) {
            return new InlineCommentResult<T>(Status.NOT_PERMITTED);
        }
        if (e instanceof NotFoundException) {
            return new InlineCommentResult<T>(Status.NOT_FOUND);
        }
        if (e instanceof BadRequestException) {
            return new InlineCommentResult<Object>(Status.BAD_REQUEST_UTF8_MYSQL_ERROR, null, e.getMessage());
        }
        return new InlineCommentResult<T>(Status.OTHER_FAILURE);
    }

    public static enum Status {
        SUCCESS,
        NOT_PERMITTED,
        REQUEST_DATA_INCORRECT,
        NOT_FOUND,
        DELETE_FAILED,
        OTHER_FAILURE,
        BAD_REQUEST_UTF8_MYSQL_ERROR;

    }
}

