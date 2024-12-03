/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.EntityTag
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.NewCookie
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$StatusType
 *  javax.ws.rs.core.Variant
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

public final class ResponseBuilderImpl
extends Response.ResponseBuilder {
    private Response.StatusType statusType = Response.Status.NO_CONTENT;
    private OutBoundHeaders headers;
    private Object entity;
    private Type entityType;

    public ResponseBuilderImpl() {
    }

    private ResponseBuilderImpl(ResponseBuilderImpl that) {
        this.statusType = that.statusType;
        this.entity = that.entity;
        this.headers = that.headers != null ? new OutBoundHeaders(that.headers) : null;
        this.entityType = that.entityType;
    }

    public Response.ResponseBuilder entityWithType(Object entity, Type entityType) {
        this.entity = entity;
        this.entityType = entityType;
        return this;
    }

    private OutBoundHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new OutBoundHeaders();
        }
        return this.headers;
    }

    public Response build() {
        ResponseImpl r = new ResponseImpl(this.statusType, this.getHeaders(), this.entity, this.entityType);
        this.reset();
        return r;
    }

    private void reset() {
        this.statusType = Response.Status.NO_CONTENT;
        this.headers = null;
        this.entity = null;
        this.entityType = null;
    }

    public Response.ResponseBuilder clone() {
        return new ResponseBuilderImpl(this);
    }

    public Response.ResponseBuilder status(Response.StatusType status) {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        this.statusType = status;
        return this;
    }

    public Response.ResponseBuilder status(int status) {
        return this.status(ResponseImpl.toStatusType(status));
    }

    public Response.ResponseBuilder entity(Object entity) {
        this.entity = entity;
        this.entityType = entity != null ? entity.getClass() : null;
        return this;
    }

    public Response.ResponseBuilder type(MediaType type) {
        this.headerSingle("Content-Type", type);
        return this;
    }

    public Response.ResponseBuilder type(String type) {
        return this.type(type == null ? null : MediaType.valueOf((String)type));
    }

    public Response.ResponseBuilder variant(Variant variant) {
        if (variant == null) {
            this.type((MediaType)null);
            this.language((String)null);
            this.encoding(null);
            return this;
        }
        this.type(variant.getMediaType());
        this.language(variant.getLanguage());
        this.encoding(variant.getEncoding());
        return this;
    }

    public Response.ResponseBuilder variants(List<Variant> variants) {
        if (variants == null) {
            this.header("Vary", null);
            return this;
        }
        if (variants.isEmpty()) {
            return this;
        }
        MediaType accept = variants.get(0).getMediaType();
        boolean vAccept = false;
        Locale acceptLanguage = variants.get(0).getLanguage();
        boolean vAcceptLanguage = false;
        String acceptEncoding = variants.get(0).getEncoding();
        boolean vAcceptEncoding = false;
        for (Variant v : variants) {
            vAccept |= !vAccept && this.vary(v.getMediaType(), accept);
            vAcceptLanguage |= !vAcceptLanguage && this.vary(v.getLanguage(), acceptLanguage);
            vAcceptEncoding |= !vAcceptEncoding && this.vary(v.getEncoding(), acceptEncoding);
        }
        StringBuilder vary = new StringBuilder();
        this.append(vary, vAccept, "Accept");
        this.append(vary, vAcceptLanguage, "Accept-Language");
        this.append(vary, vAcceptEncoding, "Accept-Encoding");
        if (vary.length() > 0) {
            this.header("Vary", vary.toString());
        }
        return this;
    }

    private boolean vary(MediaType v, MediaType vary) {
        return v != null && !v.equals((Object)vary);
    }

    private boolean vary(Locale v, Locale vary) {
        return v != null && !v.equals(vary);
    }

    private boolean vary(String v, String vary) {
        return v != null && !v.equalsIgnoreCase(vary);
    }

    private void append(StringBuilder sb, boolean v, String s) {
        if (v) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(s);
        }
    }

    public Response.ResponseBuilder language(String language) {
        this.headerSingle("Content-Language", language);
        return this;
    }

    public Response.ResponseBuilder language(Locale language) {
        this.headerSingle("Content-Language", language);
        return this;
    }

    public Response.ResponseBuilder location(URI location) {
        this.headerSingle("Location", location);
        return this;
    }

    public Response.ResponseBuilder contentLocation(URI location) {
        this.headerSingle("Content-Location", location);
        return this;
    }

    public Response.ResponseBuilder encoding(String encoding) {
        this.headerSingle("Content-Encoding", encoding);
        return this;
    }

    public Response.ResponseBuilder tag(EntityTag tag) {
        this.headerSingle("ETag", tag);
        return this;
    }

    public Response.ResponseBuilder tag(String tag) {
        return this.tag(tag == null ? null : new EntityTag(tag));
    }

    public Response.ResponseBuilder lastModified(Date lastModified) {
        this.headerSingle("Last-Modified", lastModified);
        return this;
    }

    public Response.ResponseBuilder cacheControl(CacheControl cacheControl) {
        this.headerSingle("Cache-Control", cacheControl);
        return this;
    }

    public Response.ResponseBuilder expires(Date expires) {
        this.headerSingle("Expires", expires);
        return this;
    }

    public Response.ResponseBuilder cookie(NewCookie ... cookies) {
        if (cookies != null) {
            for (NewCookie cookie : cookies) {
                this.header("Set-Cookie", cookie);
            }
        } else {
            this.header("Set-Cookie", null);
        }
        return this;
    }

    public Response.ResponseBuilder header(String name, Object value) {
        return this.header(name, value, false);
    }

    public Response.ResponseBuilder headerSingle(String name, Object value) {
        return this.header(name, value, true);
    }

    public Response.ResponseBuilder header(String name, Object value, boolean single) {
        if (value != null) {
            if (single) {
                this.getHeaders().putSingle(name, value);
            } else {
                this.getHeaders().add(name, value);
            }
        } else {
            this.getHeaders().remove(name);
        }
        return this;
    }
}

