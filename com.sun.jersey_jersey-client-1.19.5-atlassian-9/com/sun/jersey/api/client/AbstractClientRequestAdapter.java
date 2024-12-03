/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientRequestAdapter;

public abstract class AbstractClientRequestAdapter
implements ClientRequestAdapter {
    private final ClientRequestAdapter cra;

    protected AbstractClientRequestAdapter(ClientRequestAdapter cra) {
        this.cra = cra;
    }

    public ClientRequestAdapter getAdapter() {
        return this.cra;
    }
}

