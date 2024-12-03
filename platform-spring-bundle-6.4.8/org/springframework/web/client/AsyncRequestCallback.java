/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;

@FunctionalInterface
@Deprecated
public interface AsyncRequestCallback {
    public void doWithRequest(AsyncClientHttpRequest var1) throws IOException;
}

