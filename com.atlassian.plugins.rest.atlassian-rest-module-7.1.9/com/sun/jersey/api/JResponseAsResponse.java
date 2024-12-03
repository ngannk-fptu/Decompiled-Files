/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api;

import com.sun.jersey.api.JResponse;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import java.lang.reflect.Type;

public final class JResponseAsResponse
extends ResponseImpl {
    private final JResponse<?> jr;

    JResponseAsResponse(JResponse<?> jr) {
        this(jr, jr.getType());
    }

    JResponseAsResponse(JResponse<?> jr, Type type) {
        super(jr.getStatusType(), jr.getMetadata(), jr.getEntity(), type);
        this.jr = jr;
    }

    public JResponse<?> getJResponse() {
        return this.jr;
    }
}

