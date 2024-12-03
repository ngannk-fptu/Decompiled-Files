/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Cookie
 *  javax.ws.rs.core.EntityTag
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.NewCookie
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.Variant$VariantListBuilder
 *  javax.ws.rs.ext.RuntimeDelegate
 *  javax.ws.rs.ext.RuntimeDelegate$HeaderDelegate
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.core.spi.factory.VariantListBuilderImpl;
import com.sun.jersey.spi.HeaderDelegateProvider;
import com.sun.jersey.spi.service.ServiceFinder;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class AbstractRuntimeDelegate
extends RuntimeDelegate {
    private final Set<HeaderDelegateProvider> hps = new HashSet<HeaderDelegateProvider>();
    private final Map<Class<?>, RuntimeDelegate.HeaderDelegate> map = new WeakHashMap();

    public AbstractRuntimeDelegate() {
        for (HeaderDelegateProvider p : ServiceFinder.find(HeaderDelegateProvider.class, true)) {
            this.hps.add(p);
        }
        this.map.put(EntityTag.class, this._createHeaderDelegate(EntityTag.class));
        this.map.put(MediaType.class, this._createHeaderDelegate(MediaType.class));
        this.map.put(CacheControl.class, this._createHeaderDelegate(CacheControl.class));
        this.map.put(NewCookie.class, this._createHeaderDelegate(NewCookie.class));
        this.map.put(Cookie.class, this._createHeaderDelegate(Cookie.class));
        this.map.put(URI.class, this._createHeaderDelegate(URI.class));
        this.map.put(Date.class, this._createHeaderDelegate(Date.class));
        this.map.put(String.class, this._createHeaderDelegate(String.class));
    }

    public Variant.VariantListBuilder createVariantListBuilder() {
        return new VariantListBuilderImpl();
    }

    public Response.ResponseBuilder createResponseBuilder() {
        return new ResponseBuilderImpl();
    }

    public UriBuilder createUriBuilder() {
        return new UriBuilderImpl();
    }

    public <T> RuntimeDelegate.HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type parameter cannot be null");
        }
        RuntimeDelegate.HeaderDelegate h = this.map.get(type);
        if (h != null) {
            return h;
        }
        return this._createHeaderDelegate(type);
    }

    private <T> RuntimeDelegate.HeaderDelegate<T> _createHeaderDelegate(Class<T> type) {
        for (HeaderDelegateProvider hp : this.hps) {
            if (!hp.supports(type)) continue;
            return hp;
        }
        return null;
    }
}

