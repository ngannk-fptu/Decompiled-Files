/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;

public interface HttpUpgrader {
    public void prepare(HttpRequest var1);

    public void upgrade(HttpResponse var1, EndPoint var2, Callback var3);

    public static interface Factory {
        public HttpUpgrader newHttpUpgrader(HttpVersion var1);
    }
}

