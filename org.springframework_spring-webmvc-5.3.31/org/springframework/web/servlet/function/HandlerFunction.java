/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
public interface HandlerFunction<T extends ServerResponse> {
    public T handle(ServerRequest var1) throws Exception;
}

