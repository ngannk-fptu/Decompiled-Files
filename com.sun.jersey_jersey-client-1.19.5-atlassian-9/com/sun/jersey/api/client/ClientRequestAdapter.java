/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientRequest;
import java.io.IOException;
import java.io.OutputStream;

public interface ClientRequestAdapter {
    public OutputStream adapt(ClientRequest var1, OutputStream var2) throws IOException;
}

