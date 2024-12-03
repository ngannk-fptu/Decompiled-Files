/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.util;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.data.util.IteratorSpliterator;

public interface CloseableIterator<T>
extends Iterator<T>,
Closeable {
    @Override
    public void close();

    default public Spliterator<T> spliterator() {
        return new IteratorSpliterator(this);
    }

    default public Stream<T> stream() {
        return (Stream)StreamSupport.stream(this.spliterator(), false).onClose(this::close);
    }
}

