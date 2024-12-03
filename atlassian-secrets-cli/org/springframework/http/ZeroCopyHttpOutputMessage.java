/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.http;

import java.io.File;
import org.springframework.http.ReactiveHttpOutputMessage;
import reactor.core.publisher.Mono;

public interface ZeroCopyHttpOutputMessage
extends ReactiveHttpOutputMessage {
    public Mono<Void> writeWith(File var1, long var2, long var4);
}

