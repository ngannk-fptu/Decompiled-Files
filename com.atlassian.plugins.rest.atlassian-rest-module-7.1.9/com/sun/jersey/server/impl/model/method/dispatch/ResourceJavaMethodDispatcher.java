/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ResourceJavaMethodDispatcher
implements RequestDispatcher {
    protected final JavaMethodInvoker invoker;
    protected final Method method;
    private final Annotation[] annotations;

    public ResourceJavaMethodDispatcher(AbstractResourceMethod abstractResourceMethod, JavaMethodInvoker invoker) {
        this.method = abstractResourceMethod.getMethod();
        this.annotations = abstractResourceMethod.getAnnotations();
        this.invoker = invoker;
    }

    @Override
    public final void dispatch(Object resource, HttpContext context) {
        try {
            this._dispatch(resource, context);
            if (context.getResponse().getEntity() != null) {
                context.getResponse().setAnnotations(this.annotations);
            }
        }
        catch (InvocationTargetException e) {
            throw new MappableContainerException(e.getTargetException());
        }
        catch (IllegalAccessException e) {
            throw new ContainerException(e);
        }
    }

    protected abstract void _dispatch(Object var1, HttpContext var2) throws InvocationTargetException, IllegalAccessException;

    public String toString() {
        return this.method.toString();
    }
}

