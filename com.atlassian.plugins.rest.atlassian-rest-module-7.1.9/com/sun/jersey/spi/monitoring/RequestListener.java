/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.monitoring;

import com.sun.jersey.spi.container.ContainerRequest;

public interface RequestListener {
    public void onRequest(long var1, ContainerRequest var3);
}

