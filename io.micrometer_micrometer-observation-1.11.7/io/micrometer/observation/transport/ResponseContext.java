/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation.transport;

import io.micrometer.common.lang.Nullable;

public interface ResponseContext<RES> {
    @Nullable
    public RES getResponse();

    public void setResponse(RES var1);
}

