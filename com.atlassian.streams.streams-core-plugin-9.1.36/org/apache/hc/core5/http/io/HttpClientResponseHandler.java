/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io;

import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;

public interface HttpClientResponseHandler<T> {
    public T handleResponse(ClassicHttpResponse var1) throws HttpException, IOException;
}

