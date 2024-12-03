/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.interceptor.impl;

import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.plugins.rest.common.interceptor.impl.DefaultMethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.impl.InterceptorChainBuilder;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public class DispatchProviderHelper {
    static final AtomicReference<Boolean> JERSEY_291_SHIM = new AtomicReference();
    private final InterceptorChainBuilder interceptorChainBuilder;

    public DispatchProviderHelper(InterceptorChainBuilder interceptorChainBuilder) {
        this.interceptorChainBuilder = interceptorChainBuilder;
    }

    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp) {
        if (pp == null) {
            return null;
        }
        List<ResourceInterceptor> interceptors = this.interceptorChainBuilder.getResourceInterceptorsForMethod(abstractResourceMethod.getMethod());
        boolean requireReturnOfRepresentation = "GET".equals(abstractResourceMethod.getHttpMethod());
        Class<?> returnType = abstractResourceMethod.getMethod().getReturnType();
        if (Response.class.isAssignableFrom(returnType)) {
            return new ResponseOutInvoker(abstractResourceMethod, pp, interceptors);
        }
        if (returnType != Void.TYPE) {
            if (returnType == Object.class || GenericEntity.class.isAssignableFrom(returnType)) {
                return new ObjectOutInvoker(abstractResourceMethod, pp, interceptors);
            }
            return new TypeOutInvoker(abstractResourceMethod, pp, interceptors);
        }
        if (requireReturnOfRepresentation) {
            return null;
        }
        return new VoidOutInvoker(abstractResourceMethod, pp, interceptors);
    }

    static void invokeMethodWithInterceptors(List<ResourceInterceptor> originalInterceptors, AbstractResourceMethod method, Object resource, HttpContext httpContext, Object[] params, MethodInvoker methodInvocation) throws IllegalAccessException, InvocationTargetException {
        ResourceInterceptor lastInterceptor = invocation -> methodInvocation.invoke();
        ArrayList<ResourceInterceptor> interceptors = new ArrayList<ResourceInterceptor>(originalInterceptors);
        interceptors.add(lastInterceptor);
        Boolean shim = JERSEY_291_SHIM.get();
        if (shim == null) {
            shim = Boolean.getBoolean("com.atlassian.plugins.rest.shim.JERSEY-291");
            JERSEY_291_SHIM.set(shim);
        }
        if (shim.booleanValue()) {
            List<Parameter> parameterList = method.getParameters();
            for (int i = 0; i < params.length; ++i) {
                Object param;
                if (!parameterList.get(i).isAnnotationPresent(QueryParam.class) || !((param = params[i]) instanceof Collection) || !((Collection)param).isEmpty()) continue;
                params[i] = null;
            }
        }
        DefaultMethodInvocation inv = new DefaultMethodInvocation(resource, method, httpContext, interceptors, params);
        inv.invoke();
    }

    private static final class ObjectOutInvoker
    extends EntityParamInInvoker {
        ObjectOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, List<ResourceInterceptor> interceptors) {
            super(abstractResourceMethod, pp, interceptors);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            DispatchProviderHelper.invokeMethodWithInterceptors(this.interceptors, this.abstractResourceMethod, resource, context, params, () -> {
                Object o = this.method.invoke(resource, params);
                if (o instanceof Response) {
                    Response r = (Response)o;
                    context.getResponse().setResponse(r);
                } else if (o != null) {
                    Response r = new ResponseBuilderImpl().status(200).entity(o).build();
                    context.getResponse().setResponse(r);
                }
            });
        }
    }

    private static final class ResponseOutInvoker
    extends EntityParamInInvoker {
        ResponseOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, List<ResourceInterceptor> interceptors) {
            super(abstractResourceMethod, pp, interceptors);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            DispatchProviderHelper.invokeMethodWithInterceptors(this.interceptors, this.abstractResourceMethod, resource, context, params, () -> {
                Response r = (Response)this.method.invoke(resource, params);
                if (r != null) {
                    context.getResponse().setResponse(r);
                }
            });
        }
    }

    private static final class TypeOutInvoker
    extends EntityParamInInvoker {
        TypeOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, List<ResourceInterceptor> interceptors) {
            super(abstractResourceMethod, pp, interceptors);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            DispatchProviderHelper.invokeMethodWithInterceptors(this.interceptors, this.abstractResourceMethod, resource, context, params, () -> {
                Object o = this.method.invoke(resource, params);
                if (o != null) {
                    Response r = new ResponseBuilderImpl().entity(o).status(200).build();
                    context.getResponse().setResponse(r);
                }
            });
        }
    }

    private static final class VoidOutInvoker
    extends EntityParamInInvoker {
        VoidOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, List<ResourceInterceptor> interceptors) {
            super(abstractResourceMethod, pp, interceptors);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            DispatchProviderHelper.invokeMethodWithInterceptors(this.interceptors, this.abstractResourceMethod, resource, context, params, () -> this.method.invoke(resource, params));
        }
    }

    private static abstract class EntityParamInInvoker
    extends ResourceJavaMethodDispatcher {
        private final InjectableValuesProvider pp;
        final AbstractResourceMethod abstractResourceMethod;
        final List<ResourceInterceptor> interceptors;

        EntityParamInInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, List<ResourceInterceptor> interceptors) {
            super(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
            this.pp = pp;
            this.abstractResourceMethod = abstractResourceMethod;
            this.interceptors = interceptors;
        }

        final Object[] getParams(HttpContext context) {
            return this.pp.getInjectableValues(context);
        }
    }

    private static interface MethodInvoker {
        public void invoke() throws IllegalAccessException, InvocationTargetException;
    }
}

