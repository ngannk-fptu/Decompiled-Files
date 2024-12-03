/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
public interface RequestCallback {
    public void doWithRequest(ClientHttpRequest var1) throws IOException;
}

