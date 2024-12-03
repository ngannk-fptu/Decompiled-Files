/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.io.File;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;

public interface FilePart
extends Part {
    public String filename();

    public Mono<Void> transferTo(File var1);
}

