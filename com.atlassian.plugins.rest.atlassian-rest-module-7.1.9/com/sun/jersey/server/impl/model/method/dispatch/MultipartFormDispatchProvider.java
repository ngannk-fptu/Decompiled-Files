/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.model.method.dispatch.FormDispatchProvider;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.spi.inject.Injectable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class MultipartFormDispatchProvider
extends FormDispatchProvider {
    private static final Logger LOGGER = Logger.getLogger(MultipartFormDispatchProvider.class.getName());
    private static MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");

    @Override
    public RequestDispatcher create(AbstractResourceMethod method) {
        return this.create(method, JavaMethodInvokerFactory.getDefault());
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod method, JavaMethodInvoker invoker) {
        MediaType m;
        boolean found = false;
        Iterator<MediaType> iterator = method.getSupportedInputTypes().iterator();
        while (iterator.hasNext() && !(found = !(m = iterator.next()).isWildcardSubtype() && m.isCompatible(MULTIPART_FORM_DATA))) {
        }
        if (!found) {
            return null;
        }
        return super.create(method, invoker);
    }

    @Override
    protected List<Injectable> getInjectables(AbstractResourceMethod method) {
        for (int i = 0; i < method.getParameters().size(); ++i) {
            Parameter p = method.getParameters().get(i);
            if (p.getAnnotation().annotationType() != FormParam.class) continue;
            LOGGER.severe("Resource methods utilizing @FormParam and consuming \"multipart/form-data\" are no longer supported. See @FormDataParam.");
        }
        return null;
    }
}

