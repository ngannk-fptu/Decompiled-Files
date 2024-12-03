/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.StringUtil;

public abstract class Chunk {
    public static final String DEFAULT_NAME_PREFIX = "__substg1.0_";
    private final int chunkId;
    private final Types.MAPIType type;
    private final String namePrefix;

    protected Chunk(String namePrefix, int chunkId, Types.MAPIType type) {
        this.namePrefix = namePrefix;
        this.chunkId = chunkId;
        this.type = type;
    }

    protected Chunk(int chunkId, Types.MAPIType type) {
        this(DEFAULT_NAME_PREFIX, chunkId, type);
    }

    public int getChunkId() {
        return this.chunkId;
    }

    public Types.MAPIType getType() {
        return this.type;
    }

    public String getEntryName() {
        String type = this.type.asFileEnding();
        StringBuilder chunkId = new StringBuilder(Integer.toHexString(this.chunkId));
        int need0count = 4 - chunkId.length();
        if (need0count > 0) {
            chunkId.insert(0, StringUtil.repeat('0', need0count));
        }
        return this.namePrefix + chunkId.toString().toUpperCase(Locale.ROOT) + type.toUpperCase(Locale.ROOT);
    }

    public abstract void writeValue(OutputStream var1) throws IOException;

    public abstract void readValue(InputStream var1) throws IOException;
}

