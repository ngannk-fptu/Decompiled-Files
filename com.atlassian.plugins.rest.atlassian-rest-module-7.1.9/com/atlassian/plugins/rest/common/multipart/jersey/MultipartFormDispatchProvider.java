/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart.jersey;

import com.atlassian.plugins.rest.common.interceptor.impl.DispatchProviderHelper;
import com.atlassian.plugins.rest.common.interceptor.impl.InterceptorChainBuilder;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartForm;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.server.impl.model.method.dispatch.AbstractResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class MultipartFormDispatchProvider
extends AbstractResourceMethodDispatchProvider {
    private static final String MULTIPART_FORM_PROPERTY = "com.atlassian.rest.multipart.form";
    @Context
    private InterceptorChainBuilder interceptorChainBuilder;

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        DispatchProviderHelper helper = new DispatchProviderHelper(this.interceptorChainBuilder);
        return helper.create(abstractResourceMethod, this.getInjectableValuesProvider(abstractResourceMethod));
    }

    @Override
    protected InjectableValuesProvider getInjectableValuesProvider(final AbstractResourceMethod method) {
        for (Parameter param : method.getParameters()) {
            for (Annotation annotation : param.getAnnotations()) {
                if (!(annotation instanceof MultipartFormParam)) continue;
                return new InjectableValuesProvider(this.getInjectables(method)){

                    @Override
                    public Object[] getInjectableValues(HttpContext context) {
                        if (!context.getProperties().containsKey(MultipartFormDispatchProvider.MULTIPART_FORM_PROPERTY)) {
                            context.getProperties().put(MultipartFormDispatchProvider.MULTIPART_FORM_PROPERTY, context.getRequest().getEntity(MultipartForm.class, (Type)((Object)MultipartForm.class), method.getAnnotations()));
                        }
                        return super.getInjectableValues(context);
                    }
                };
            }
        }
        return null;
    }

    private List<Injectable> getInjectables(AbstractResourceMethod method) {
        ArrayList<Injectable> is = new ArrayList<Injectable>(method.getParameters().size());
        for (int i = 0; i < method.getParameters().size(); ++i) {
            Parameter parameter = method.getParameters().get(i);
            Injectable<?> injectable = null;
            if (Parameter.Source.ENTITY == parameter.getSource()) {
                return null;
            }
            for (Annotation annotation : parameter.getAnnotations()) {
                if (!(annotation instanceof MultipartFormParam)) continue;
                injectable = this.getMultipartFormInjectable(parameter, (MultipartFormParam)annotation);
            }
            if (injectable == null) {
                injectable = this.getInjectableProviderContext().getInjectable(parameter, ComponentScope.PerRequest);
            }
            if (injectable == null) {
                return null;
            }
            is.add(injectable);
        }
        return is;
    }

    private Injectable<?> getMultipartFormInjectable(final Parameter parameter, final MultipartFormParam annotation) {
        if (parameter.getParameterClass().equals(FilePart.class)) {
            return new AbstractHttpContextInjectable<FilePart>(){

                @Override
                public FilePart getValue(HttpContext context) {
                    return ((MultipartForm)context.getProperties().get(MultipartFormDispatchProvider.MULTIPART_FORM_PROPERTY)).getFilePart(annotation.value());
                }
            };
        }
        if (Collection.class.isAssignableFrom(parameter.getParameterClass())) {
            return new AbstractHttpContextInjectable<Collection<FilePart>>(){

                @Override
                public Collection<FilePart> getValue(HttpContext context) {
                    Collection<FilePart> parts = ((MultipartForm)context.getProperties().get(MultipartFormDispatchProvider.MULTIPART_FORM_PROPERTY)).getFileParts(annotation.value());
                    if (parameter.getParameterClass().isAssignableFrom(Collection.class)) {
                        return parts;
                    }
                    if (parameter.getParameterClass().isAssignableFrom(List.class)) {
                        return new ArrayList<FilePart>(parts);
                    }
                    if (parameter.getParameterClass().isAssignableFrom(Set.class)) {
                        return new HashSet<FilePart>(parts);
                    }
                    return null;
                }
            };
        }
        return null;
    }
}

