/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public final class HttpContextInjectableProvider
implements InjectableProvider<Context, Type> {
    private final Map<Type, Injectable> injectables = new HashMap<Type, Injectable>();

    public HttpContextInjectableProvider() {
        HttpContextRequestInjectable re = new HttpContextRequestInjectable();
        this.injectables.put((Type)((Object)HttpHeaders.class), re);
        this.injectables.put((Type)((Object)Request.class), re);
        this.injectables.put((Type)((Object)SecurityContext.class), re);
        this.injectables.put((Type)((Object)HttpContext.class), new HttpContextInjectable());
        this.injectables.put((Type)((Object)UriInfo.class), new UriInfoInjectable());
        this.injectables.put((Type)((Object)ExtendedUriInfo.class), new UriInfoInjectable());
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context a, Type c) {
        return this.injectables.get(c);
    }

    private static final class UriInfoInjectable
    extends AbstractHttpContextInjectable<UriInfo> {
        private UriInfoInjectable() {
        }

        @Override
        public UriInfo getValue(HttpContext context) {
            return context.getUriInfo();
        }
    }

    private static final class HttpContextRequestInjectable
    extends AbstractHttpContextInjectable<Object> {
        private HttpContextRequestInjectable() {
        }

        @Override
        public Object getValue(HttpContext context) {
            return context.getRequest();
        }
    }

    private static final class HttpContextInjectable
    extends AbstractHttpContextInjectable<Object> {
        private HttpContextInjectable() {
        }

        @Override
        public Object getValue(HttpContext context) {
            return context;
        }
    }
}

