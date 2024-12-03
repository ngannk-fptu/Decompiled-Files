/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.server.impl.model.method.ResourceHttpOptionsMethod;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.research.ws.wadl.Application;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

final class WadlMethodFactory {
    WadlMethodFactory() {
    }

    private static final class WadlOptionsMethodDispatcher
    extends ResourceHttpOptionsMethod.OptionsRequestDispatcher {
        private final AbstractResource resource;
        private final String path;
        private final WadlApplicationContext wadlApplicationContext;
        private final String lastModified;
        private static final Logger LOGGER = Logger.getLogger(WadlOptionsMethodDispatcher.class.getName());

        WadlOptionsMethodDispatcher(Map<String, List<ResourceMethod>> methods, AbstractResource resource, String path, WadlApplicationContext wadlApplicationContext) {
            super(methods);
            this.resource = resource;
            this.path = path;
            this.wadlApplicationContext = wadlApplicationContext;
            this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
        }

        @Override
        public void dispatch(Object o, HttpContext context) {
            if (this.wadlApplicationContext.isWadlGenerationEnabled()) {
                Application a = this.wadlApplicationContext.getApplication(context.getUriInfo(), this.resource, this.path);
                List<Variant> vl = Variant.mediaTypes(MediaTypes.WADL, MediaTypes.WADL_JSON, MediaType.APPLICATION_XML_TYPE).add().build();
                Variant v = context.getRequest().selectVariant(vl);
                if (v == null) {
                    context.getResponse().setResponse(Response.noContent().header("Allow", this.allow).header("Last-modified", this.lastModified).build());
                } else {
                    try {
                        context.getResponse().setResponse(Response.ok((Object)a, v).header("Allow", this.allow).header("Last-modified", this.lastModified).build());
                    }
                    catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Could not marshal wadl Application.", e);
                        context.getResponse().setResponse(Response.noContent().header("Allow", this.allow).build());
                    }
                }
            } else {
                context.getResponse().setResponse(Response.noContent().header("Allow", this.allow).build());
            }
        }
    }

    public static final class WadlOptionsMethod
    extends ResourceMethod {
        public WadlOptionsMethod(Map<String, List<ResourceMethod>> methods, AbstractResource resource, String path, WadlApplicationContext wadlApplicationContext) {
            super("OPTIONS", UriTemplate.EMPTY, MediaTypes.GENERAL_MEDIA_TYPE_LIST, MediaTypes.GENERAL_MEDIA_TYPE_LIST, false, new WadlOptionsMethodDispatcher(methods, resource, path, wadlApplicationContext));
        }

        public String toString() {
            return "WADL OPTIONS method";
        }
    }
}

