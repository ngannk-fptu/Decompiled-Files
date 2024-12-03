/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.hdgf.streams.StreamStore;

public final class UnknownStream
extends Stream {
    protected UnknownStream(Pointer pointer, StreamStore store) {
        super(pointer, store);
    }
}

