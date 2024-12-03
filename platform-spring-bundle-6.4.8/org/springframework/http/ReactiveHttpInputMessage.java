/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.http;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMessage;
import reactor.core.publisher.Flux;

public interface ReactiveHttpInputMessage
extends HttpMessage {
    public Flux<DataBuffer> getBody();
}

