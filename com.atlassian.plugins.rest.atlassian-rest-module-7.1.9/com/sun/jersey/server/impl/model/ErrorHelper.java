/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.impl.ImplMessages;
import java.lang.reflect.Method;
import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Produces;

public final class ErrorHelper {
    public static ContainerException objectNotAWebResource(Class resourceClass) {
        return new ContainerException(ImplMessages.OBJECT_NOT_A_WEB_RESOURCE(resourceClass.getName()));
    }

    public static ContainerException badClassConsumes(Exception e, Class resourceClass, Consumes c) {
        return new ContainerException(ImplMessages.BAD_CLASS_CONSUMEMIME(resourceClass, c.value()), e);
    }

    public static ContainerException badClassProduces(Exception e, Class resourceClass, Produces p) {
        return new ContainerException(ImplMessages.BAD_CLASS_PRODUCEMIME(resourceClass, p.value()), e);
    }

    public static ContainerException badMethodHttpMethod(Class resourceClass, Method m, HttpMethod hm) {
        return new ContainerException(ImplMessages.BAD_METHOD_HTTPMETHOD(resourceClass, hm.value(), m.toString()));
    }

    public static ContainerException badMethodConsumes(Exception e, Class resourceClass, Method m, Consumes c) {
        return new ContainerException(ImplMessages.BAD_METHOD_CONSUMEMIME(resourceClass, c.value(), m.toString()), e);
    }

    public static ContainerException badMethodProduces(Exception e, Class resourceClass, Method m, Produces p) {
        return new ContainerException(ImplMessages.BAD_METHOD_PRODUCEMIME(resourceClass, p.value(), m.toString()), e);
    }
}

