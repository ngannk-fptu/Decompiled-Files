/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.exceptions;

public final class ChunkNotFoundException
extends Exception {
    private static final long serialVersionUID = 1L;

    public ChunkNotFoundException() {
        super("Chunk not found");
    }

    public ChunkNotFoundException(String chunkName) {
        super(chunkName + " was named, but not found in POIFS object");
    }
}

