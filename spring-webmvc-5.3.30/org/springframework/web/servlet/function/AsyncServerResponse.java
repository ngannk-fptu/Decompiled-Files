/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.time.Duration;
import org.springframework.web.servlet.function.DefaultAsyncServerResponse;
import org.springframework.web.servlet.function.ServerResponse;

public interface AsyncServerResponse
extends ServerResponse {
    public ServerResponse block();

    public static AsyncServerResponse create(Object asyncResponse) {
        return DefaultAsyncServerResponse.create(asyncResponse, null);
    }

    public static AsyncServerResponse create(Object asyncResponse, Duration timeout) {
        return DefaultAsyncServerResponse.create(asyncResponse, timeout);
    }
}

