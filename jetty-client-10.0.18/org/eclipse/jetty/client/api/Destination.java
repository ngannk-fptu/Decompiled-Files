/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Promise
 */
package org.eclipse.jetty.client.api;

import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Promise;

public interface Destination {
    public String getScheme();

    public String getHost();

    public int getPort();

    public void newConnection(Promise<Connection> var1);
}

