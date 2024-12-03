/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.http;

import java.io.File;
import java.nio.file.Path;
import org.springframework.http.ReactiveHttpOutputMessage;
import reactor.core.publisher.Mono;

public interface ZeroCopyHttpOutputMessage
extends ReactiveHttpOutputMessage {
    default public Mono<Void> writeWith(File file, long position, long count) {
        return this.writeWith(file.toPath(), position, count);
    }

    public Mono<Void> writeWith(Path var1, long var2, long var4);
}

