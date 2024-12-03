/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.util.IOUtils;

public class ByteChunkDeferred
extends ByteChunk {
    private DocumentNode node;

    public ByteChunkDeferred(String namePrefix, int chunkId, Types.MAPIType type) {
        super(namePrefix, chunkId, type);
    }

    public void readValue(DocumentNode node) {
        this.node = node;
    }

    @Override
    public void readValue(InputStream value) throws IOException {
        if (this.node == null) {
            super.readValue(value);
        }
    }

    @Override
    public void writeValue(OutputStream out) throws IOException {
        if (this.node == null) {
            super.writeValue(out);
            return;
        }
        try (DocumentInputStream dis = this.createDocumentInputStream();){
            IOUtils.copy((InputStream)dis, out);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] getValue() {
        if (this.node == null) {
            return super.getValue();
        }
        try (DocumentInputStream dis = this.createDocumentInputStream();){
            byte[] byArray = IOUtils.toByteArray(dis, this.node.getSize());
            return byArray;
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public void setValue(byte[] value) {
        this.node = null;
        super.setValue(value);
    }

    private DocumentInputStream createDocumentInputStream() throws IOException {
        return ((DirectoryNode)this.node.getParent()).createDocumentInputStream(this.node);
    }
}

