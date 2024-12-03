/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.poifs.filesystem.DirectoryNode;

public class DirectoryChunk
extends Chunk {
    private DirectoryNode dir;

    public DirectoryChunk(DirectoryNode dir, String namePrefix, int chunkId, Types.MAPIType type) {
        super(namePrefix, chunkId, type);
        this.dir = dir;
    }

    public DirectoryNode getDirectory() {
        return this.dir;
    }

    public MAPIMessage getAsEmbeddedMessage() throws IOException {
        return new MAPIMessage(this.dir);
    }

    @Override
    public void readValue(InputStream value) {
    }

    @Override
    public void writeValue(OutputStream out) {
    }
}

