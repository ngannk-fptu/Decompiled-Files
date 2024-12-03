/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.urlconnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface HttpURLConnectionFactory {
    public HttpURLConnection getHttpURLConnection(URL var1) throws IOException;
}

