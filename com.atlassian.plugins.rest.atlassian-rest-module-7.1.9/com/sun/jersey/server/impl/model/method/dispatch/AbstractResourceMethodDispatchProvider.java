/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.JResponse;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public abstract class AbstractResourceMethodDispatchProvider
implements ResourceMethodDispatchProvider,
ResourceMethodCustomInvokerDispatchProvider {
    @Context
    private ServerInjectableProviderContext sipc;

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        InjectableValuesProvider pp = this.getInjectableValuesProvider(abstractResourceMethod);
        if (pp == null) {
            return null;
        }
        if (pp.getInjectables().contains(null)) {
            for (int i = 0; i < pp.getInjectables().size(); ++i) {
                if (pp.getInjectables().get(i) != null) continue;
                Errors.missingDependency(abstractResourceMethod.getMethod(), i);
            }
            return null;
        }
        Class returnType = abstractResourceMethod.getReturnType();
        if (Response.class.isAssignableFrom(returnType)) {
            return new ResponseOutInvoker(abstractResourceMethod, pp, invoker);
        }
        if (JResponse.class.isAssignableFrom(returnType)) {
            return new JResponseOutInvoker(abstractResourceMethod, pp, invoker);
        }
        if (returnType != Void.TYPE) {
            if (returnType == Object.class || GenericEntity.class.isAssignableFrom(returnType)) {
                return new ObjectOutInvoker(abstractResourceMethod, pp, invoker);
            }
            return new TypeOutInvoker(abstractResourceMethod, pp, invoker);
        }
        return new VoidOutInvoker(abstractResourceMethod, pp, invoker);
    }

    protected ServerInjectableProviderContext getInjectableProviderContext() {
        return this.sipc;
    }

    protected abstract InjectableValuesProvider getInjectableValuesProvider(AbstractResourceMethod var1);

    private static final class ObjectOutInvoker
    extends EntityParamInInvoker {
        ObjectOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            Object o = this.invoker.invoke(this.method, resource, params);
            if (o instanceof Response) {
                context.getResponse().setResponse((Response)o);
            } else if (o instanceof JResponse) {
                context.getResponse().setResponse(((JResponse)o).toResponse());
            } else if (o != null) {
                Response r = new ResponseBuilderImpl().status(200).entity(o).build();
                context.getResponse().setResponse(r);
            }
        }
    }

    private static final class JResponseOutInvoker
    extends EntityParamInInvoker {
        private final Type t;

        JResponseOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp);
            ParameterizedType pt;
            Type jResponseType = abstractResourceMethod.getGenericReturnType();
            this.t = jResponseType instanceof ParameterizedType ? ((pt = (ParameterizedType)jResponseType).getRawType().equals(JResponse.class) ? ((ParameterizedType)jResponseType).getActualTypeArguments()[0] : null) : null;
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            JResponse r = (JResponse)this.invoker.invoke(this.method, resource, params);
            if (r != null) {
                if (this.t == null) {
                    context.getResponse().setResponse(r.toResponse());
                } else {
                    context.getResponse().setResponse(r.toResponse(this.t));
                }
            }
        }
    }

    private static final class ResponseOutInvoker
    extends EntityParamInInvoker {
        ResponseOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            Response r = (Response)this.invoker.invoke(this.method, resource, params);
            if (r != null) {
                context.getResponse().setResponse(r);
            }
        }
    }

    private static final class TypeOutInvoker
    extends EntityParamInInvoker {
        private final Type t;

        TypeOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
            this.t = abstractResourceMethod.getGenericReturnType();
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            Object o = this.invoker.invoke(this.method, resource, params);
            if (o != null) {
                Response r = new ResponseBuilderImpl().entityWithType(o, this.t).status(200).build();
                context.getResponse().setResponse(r);
            }
        }
    }

    private static final class VoidOutInvoker
    extends EntityParamInInvoker {
        VoidOutInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            Object[] params = this.getParams(context);
            this.invoker.invoke(this.method, resource, params);
        }
    }

    private static abstract class EntityParamInInvoker
    extends ResourceJavaMethodDispatcher {
        private final InjectableValuesProvider pp;

        EntityParamInInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp) {
            this(abstractResourceMethod, pp, JavaMethodInvokerFactory.getDefault());
        }

        EntityParamInInvoker(AbstractResourceMethod abstractResourceMethod, InjectableValuesProvider pp, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
            this.pp = pp;
        }

        final Object[] getParams(HttpContext context) {
            return this.pp.getInjectableValues(context);
        }
    }
}

