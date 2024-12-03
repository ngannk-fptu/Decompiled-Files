/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;

public interface ProtocolHandler {
    public String getName();

    public boolean accept(Request var1, Response var2);

    public Response.Listener getResponseListener();
}

