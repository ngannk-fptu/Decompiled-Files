/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.PropertiesChunk;
import org.apache.poi.util.LittleEndian;

public class StoragePropertiesChunk
extends PropertiesChunk {
    public StoragePropertiesChunk(ChunkGroup parentGroup) {
        super(parentGroup);
    }

    @Override
    public void readValue(InputStream stream) throws IOException {
        LittleEndian.readLong(stream);
        this.readProperties(stream);
    }

    @Override
    public void writeValue(OutputStream out) throws IOException {
        out.write(new byte[8]);
        this.writeProperties(out);
    }
}

