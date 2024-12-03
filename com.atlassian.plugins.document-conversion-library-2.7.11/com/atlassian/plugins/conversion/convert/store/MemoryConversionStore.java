/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.store;

import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryConversionStore
implements ConversionStore {
    private final Map<UUID, byte[]> memory = new HashMap<UUID, byte[]>();

    @Override
    public OutputStream createFile(final UUID uuid) {
        return new ByteArrayOutputStream(){

            @Override
            public void close() throws IOException {
                super.close();
                MemoryConversionStore.this.memory.put(uuid, this.buf);
            }
        };
    }

    @Override
    public InputStream readFile(UUID uuid) {
        byte[] bytes = this.memory.get(uuid);
        return bytes != null ? new ByteArrayInputStream(bytes) : null;
    }
}

