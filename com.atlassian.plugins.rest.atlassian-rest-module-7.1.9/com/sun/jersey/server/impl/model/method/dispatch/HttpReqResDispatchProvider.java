/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class HttpReqResDispatchProvider
implements ResourceMethodDispatchProvider,
ResourceMethodCustomInvokerDispatchProvider {
    private static final Class[] EXPECTED_METHOD_PARAMS = new Class[]{HttpRequestContext.class, HttpResponseContext.class};

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        if (abstractResourceMethod.getMethod().getReturnType() != Void.TYPE) {
            return null;
        }
        Object[] parameters = abstractResourceMethod.getMethod().getParameterTypes();
        if (!Arrays.deepEquals(parameters, EXPECTED_METHOD_PARAMS)) {
            return null;
        }
        return new HttpReqResDispatcher(abstractResourceMethod, invoker);
    }

    static final class HttpReqResDispatcher
    extends ResourceJavaMethodDispatcher {
        HttpReqResDispatcher(AbstractResourceMethod abstractResourceMethod) {
            this(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
        }

        HttpReqResDispatcher(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws InvocationTargetException, IllegalAccessException {
            this.invoker.invoke(this.method, resource, context.getRequest(), context.getResponse());
        }
    }
}

