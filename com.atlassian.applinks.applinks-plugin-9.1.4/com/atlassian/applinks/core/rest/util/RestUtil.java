/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.Status
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.core.rest.util;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.rest.model.ErrorListEntity;
import com.atlassian.applinks.core.rest.util.BadParameterException;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.Status;
import com.atlassian.sal.api.message.I18nResolver;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.Response;

public final class RestUtil {
    public static final String REST_APPLINKS_URL = "/rest/applinks/1.0/";

    private RestUtil() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    public static Response ok() {
        return Response.ok().entity((Object)Status.ok().build()).build();
    }

    public static Response ok(String message) {
        return Response.ok().entity((Object)Status.ok().message(message).build()).build();
    }

    public static Response ok(Object entity) {
        return Response.ok((Object)entity).build();
    }

    public static Response noContent() {
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }

    public static Response badRequest(String ... errors) {
        return Response.status((int)400).entity((Object)new ErrorListEntity(400, errors)).build();
    }

    public static Response badFormRequest(List<String> errors, List<String> fields) {
        return Response.status((int)400).entity((Object)new ErrorListEntity(400, errors, fields)).build();
    }

    public static Response serverError(String message) {
        return Response.status((int)500).entity((Object)Status.error().message(message).build()).build();
    }

    public static Response notFound(String message) {
        return Response.status((int)404).entity((Object)Status.notFound().message(message).build()).build();
    }

    public static Response unauthorized(String message) {
        return Response.status((int)401).entity((Object)Status.unauthorized().message(message).build()).build();
    }

    public static Response forbidden(String message) {
        return Response.status((int)403).entity((Object)Status.forbidden().message(message).build()).build();
    }

    public static Response conflict(String message) {
        return Response.status((int)409).entity((Object)new ErrorListEntity(409, message)).build();
    }

    public static Response credentialsRequired(I18nResolver i18nResolver) {
        return RestUtil.unauthorized(i18nResolver.getText("applinks.remote.operation.failed.credentials.required"));
    }

    public static Response created(Link link) {
        return Response.status((int)201).entity((Object)Status.created((Link)link).build()).build();
    }

    public static Response created() {
        return Response.status((int)201).build();
    }

    public static Response updated(Link link) {
        return Response.ok((Object)Status.ok().updated(link).build()).build();
    }

    public static Response updated(Link link, String message) {
        return Response.ok((Object)Status.ok().updated(link).message(message).build()).build();
    }

    public static void checkParam(String name, Object value) {
        if (value == null) {
            throw new BadParameterException(name);
        }
    }

    public static URI getBaseRestUri(ApplicationLink applicationLink) {
        return RestUtil.getBaseRestUri(applicationLink.getRpcUrl());
    }

    public static URI getBaseRestUri(URI baseUri) {
        try {
            return Uris.concatenate(baseUri, "/rest/applinks/1.0");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to add REST base path to baseUri: %s", baseUri.toASCIIString()), e);
        }
    }

    public static Response typeNotInstalled(TypeId typeId) {
        return RestUtil.badRequest(String.format("No type with id %s installed", typeId));
    }
}

