/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.SendFailure;
import org.eclipse.jetty.client.api.Connection;

public interface IConnection
extends Connection {
    public SendFailure send(HttpExchange var1);
}

