/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.io.File;
import java.nio.file.Path;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;

public interface FilePart
extends Part {
    public String filename();

    default public Mono<Void> transferTo(File dest) {
        return this.transferTo(dest.toPath());
    }

    public Mono<Void> transferTo(Path var1);
}

