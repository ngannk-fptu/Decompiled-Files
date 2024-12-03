/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.StringChunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.IOUtils;

public class ByteChunk
extends Chunk {
    private byte[] value;

    public ByteChunk(String namePrefix, int chunkId, Types.MAPIType type) {
        super(namePrefix, chunkId, type);
    }

    public ByteChunk(int chunkId, Types.MAPIType type) {
        super(chunkId, type);
    }

    @Override
    public void readValue(InputStream value) throws IOException {
        this.value = IOUtils.toByteArray(value);
    }

    @Override
    public void writeValue(OutputStream out) throws IOException {
        out.write(this.value);
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String toString() {
        return ByteChunk.toDebugFriendlyString(this.value);
    }

    protected static String toDebugFriendlyString(byte[] value) {
        if (value == null) {
            return "(Null Byte Array)";
        }
        StringBuilder text = new StringBuilder();
        text.append("Bytes len=").append(value.length);
        text.append(" [");
        int limit = Math.min(value.length, 16);
        if (value.length > 16) {
            limit = 12;
        }
        for (int i = 0; i < limit; ++i) {
            if (i > 0) {
                text.append(',');
            }
            text.append(value[i]);
        }
        if (value.length > 16) {
            text.append(",....");
        }
        text.append("]");
        return text.toString();
    }

    public String getAs7bitString() {
        return StringChunk.parseAs7BitData(this.value);
    }
}

