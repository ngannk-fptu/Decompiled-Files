/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.api;

import java.io.Closeable;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;

public interface Connection
extends Closeable {
    public void send(Request var1, Response.CompleteListener var2);

    @Override
    public void close();

    public boolean isClosed();
}

