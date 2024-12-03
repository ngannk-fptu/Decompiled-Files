/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.Origin;

public class DuplexHttpDestination
extends HttpDestination {
    public DuplexHttpDestination(HttpClient client, Origin origin) {
        this(client, origin, false);
    }

    public DuplexHttpDestination(HttpClient client, Origin origin, boolean intrinsicallySecure) {
        super(client, origin, intrinsicallySecure);
    }
}

