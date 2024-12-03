/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.stream.Stream;
import org.apache.tools.ant.types.Resource;

public interface ResourceCollection
extends Iterable<Resource> {
    public int size();

    public boolean isFilesystemOnly();

    default public Stream<? extends Resource> stream() {
        Stream.Builder b = Stream.builder();
        this.forEach(b);
        return b.build();
    }

    default public boolean isEmpty() {
        return this.size() == 0;
    }
}

