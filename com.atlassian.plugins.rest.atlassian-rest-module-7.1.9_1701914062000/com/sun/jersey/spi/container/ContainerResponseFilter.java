/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

public interface ContainerResponseFilter {
    public ContainerResponse filter(ContainerRequest var1, ContainerResponse var2);
}

