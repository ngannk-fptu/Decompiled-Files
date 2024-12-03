/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.timers.client;

import com.amazonaws.Response;

public class SdkInterruptedException
extends InterruptedException {
    private static final long serialVersionUID = 8194951388566545094L;
    private final transient Response<?> response;

    public SdkInterruptedException(Response<?> response) {
        this.response = response;
    }

    public Response<?> getResponse() {
        return this.response;
    }
}

