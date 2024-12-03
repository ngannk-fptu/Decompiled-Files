/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.monitoring;

import com.sun.jersey.spi.container.ContainerResponse;
import javax.ws.rs.ext.ExceptionMapper;

public interface ResponseListener {
    public void onError(long var1, Throwable var3);

    public void onResponse(long var1, ContainerResponse var3);

    public void onMappedException(long var1, Throwable var3, ExceptionMapper var4);
}

