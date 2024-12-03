/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationProvider;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.Iterator;

public final class WebApplicationFactory {
    private WebApplicationFactory() {
    }

    public static WebApplication createWebApplication() throws ContainerException {
        Iterator<WebApplicationProvider> iterator = ServiceFinder.find(WebApplicationProvider.class).iterator();
        if (iterator.hasNext()) {
            WebApplicationProvider wap = iterator.next();
            return wap.createWebApplication();
        }
        throw new ContainerException("No WebApplication provider is present");
    }
}

