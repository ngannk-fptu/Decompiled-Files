/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.lang.reflect.InvocationTargetException;

public class VoidVoidDispatchProvider
implements ResourceMethodDispatchProvider,
ResourceMethodCustomInvokerDispatchProvider {
    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        if (!abstractResourceMethod.getParameters().isEmpty()) {
            return null;
        }
        if (abstractResourceMethod.getReturnType() != Void.TYPE) {
            return null;
        }
        return new VoidVoidMethodInvoker(abstractResourceMethod, invoker);
    }

    public static final class VoidVoidMethodInvoker
    extends ResourceJavaMethodDispatcher {
        public VoidVoidMethodInvoker(AbstractResourceMethod abstractResourceMethod) {
            this(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
        }

        public VoidVoidMethodInvoker(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
        }

        @Override
        public void _dispatch(Object resource, HttpContext context) throws IllegalAccessException, InvocationTargetException {
            this.invoker.invoke(this.method, resource, new Object[0]);
        }
    }
}

