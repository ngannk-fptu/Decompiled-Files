/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Experimental
 *  org.apache.hc.core5.http.HttpResponse
 */
package org.apache.hc.client5.http.impl.classic;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.apache.hc.client5.http.classic.ConnectionBackoffStrategy;
import org.apache.hc.core5.annotation.Experimental;
import org.apache.hc.core5.http.HttpResponse;

@Experimental
public class DefaultBackoffStrategy
implements ConnectionBackoffStrategy {
    @Override
    public boolean shouldBackoff(Throwable t) {
        return t instanceof SocketTimeoutException || t instanceof ConnectException;
    }

    @Override
    public boolean shouldBackoff(HttpResponse response) {
        return response.getCode() == 429 || response.getCode() == 503;
    }
}

