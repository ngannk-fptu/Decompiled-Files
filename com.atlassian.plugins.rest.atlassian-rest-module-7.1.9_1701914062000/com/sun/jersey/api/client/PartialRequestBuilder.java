/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.core.header.OutBoundHeaders;
import java.util.Locale;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public abstract class PartialRequestBuilder<T extends RequestBuilder>
implements RequestBuilder<T> {
    protected Object entity;
    protected MultivaluedMap<String, Object> metadata = new OutBoundHeaders();

    protected PartialRequestBuilder() {
    }

    @Override
    public T entity(Object entity) {
        this.entity = entity;
        return (T)this;
    }

    @Override
    public T entity(Object entity, MediaType type) {
        this.entity(entity);
        this.type(type);
        return (T)this;
    }

    @Override
    public T entity(Object entity, String type) {
        this.entity(entity);
        this.type(type);
        return (T)this;
    }

    @Override
    public T type(MediaType type) {
        this.getMetadata().putSingle("Content-Type", type);
        return (T)this;
    }

    @Override
    public T type(String type) {
        this.getMetadata().putSingle("Content-Type", MediaType.valueOf(type));
        return (T)this;
    }

    @Override
    public T accept(MediaType ... types) {
        for (MediaType type : types) {
            this.getMetadata().add("Accept", type);
        }
        return (T)this;
    }

    @Override
    public T accept(String ... types) {
        for (String type : types) {
            this.getMetadata().add("Accept", type);
        }
        return (T)this;
    }

    @Override
    public T acceptLanguage(Locale ... locales) {
        for (Locale locale : locales) {
            this.getMetadata().add("Accept-Language", locale);
        }
        return (T)this;
    }

    @Override
    public T acceptLanguage(String ... locales) {
        for (String locale : locales) {
            this.getMetadata().add("Accept-Language", locale);
        }
        return (T)this;
    }

    @Override
    public T cookie(Cookie cookie) {
        this.getMetadata().add("Cookie", cookie);
        return (T)this;
    }

    @Override
    public T header(String name, Object value) {
        this.getMetadata().add(name, value);
        return (T)this;
    }

    private MultivaluedMap<String, Object> getMetadata() {
        if (this.metadata != null) {
            return this.metadata;
        }
        this.metadata = new OutBoundHeaders();
        return this.metadata;
    }
}

