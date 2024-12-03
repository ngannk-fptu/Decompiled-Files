/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import javax.xml.ws.Response;

public interface AsyncHandler<T> {
    public void handleResponse(Response<T> var1);
}

