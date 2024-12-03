/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ContainerResponse;
import java.io.IOException;
import java.io.OutputStream;

public interface ContainerResponseWriter {
    public OutputStream writeStatusAndHeaders(long var1, ContainerResponse var3) throws IOException;

    public void finish() throws IOException;
}

