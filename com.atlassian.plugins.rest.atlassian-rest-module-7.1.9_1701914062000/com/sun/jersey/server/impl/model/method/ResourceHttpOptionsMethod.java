/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;

public final class ResourceHttpOptionsMethod
extends ResourceMethod {
    public ResourceHttpOptionsMethod(Map<String, List<ResourceMethod>> methods) {
        super("OPTIONS", UriTemplate.EMPTY, MediaTypes.GENERAL_MEDIA_TYPE_LIST, MediaTypes.GENERAL_MEDIA_TYPE_LIST, false, new OptionsRequestDispatcher(methods));
    }

    public String toString() {
        return "OPTIONS";
    }

    public static class OptionsRequestDispatcher
    implements RequestDispatcher {
        protected final String allow;

        public OptionsRequestDispatcher(Map<String, List<ResourceMethod>> methods) {
            this.allow = this.getAllow(methods);
        }

        private String getAllow(Map<String, List<ResourceMethod>> methods) {
            StringBuilder s = new StringBuilder("OPTIONS");
            for (String method : methods.keySet()) {
                s.append(',').append(method);
            }
            return s.toString();
        }

        @Override
        public void dispatch(Object resource, HttpContext context) {
            Response r = Response.noContent().header("Allow", this.allow).build();
            context.getResponse().setResponse(r);
        }
    }
}

