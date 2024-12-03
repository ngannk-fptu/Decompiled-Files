/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;

public interface WebApplicationProvider {
    public WebApplication createWebApplication() throws ContainerException;
}

