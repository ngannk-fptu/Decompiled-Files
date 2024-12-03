/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceContext
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.ResourceInjector;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.util.InjectionPlan;
import javax.xml.ws.WebServiceContext;

public final class DefaultResourceInjector
extends ResourceInjector {
    @Override
    public void inject(@NotNull WSWebServiceContext context, @NotNull Object instance) {
        InjectionPlan.buildInjectionPlan(instance.getClass(), WebServiceContext.class, false).inject(instance, context);
    }
}

