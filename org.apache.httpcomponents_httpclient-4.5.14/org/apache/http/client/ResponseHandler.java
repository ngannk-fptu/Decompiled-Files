/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public interface ResponseHandler<T> {
    public T handleResponse(HttpResponse var1) throws ClientProtocolException, IOException;
}

