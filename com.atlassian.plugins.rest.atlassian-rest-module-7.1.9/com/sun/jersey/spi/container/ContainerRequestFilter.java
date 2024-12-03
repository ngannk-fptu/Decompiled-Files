/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ContainerRequest;

public interface ContainerRequestFilter {
    public ContainerRequest filter(ContainerRequest var1);
}

