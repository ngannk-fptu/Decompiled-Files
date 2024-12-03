/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import javax.ws.rs.ext.ExceptionMapper;

public interface ExceptionMapperContext {
    public ExceptionMapper find(Class<? extends Throwable> var1);
}

