/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart.jersey;

import com.atlassian.plugins.rest.common.multipart.MultipartHandler;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class MultipartHandlerInjectableProvider
extends SingletonTypeInjectableProvider<Context, MultipartHandler> {
    public MultipartHandlerInjectableProvider(MultipartHandler handler) {
        super((Type)((Object)MultipartHandler.class), handler);
    }
}

