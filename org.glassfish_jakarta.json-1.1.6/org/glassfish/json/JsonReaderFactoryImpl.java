/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import org.glassfish.json.JsonReaderImpl;
import org.glassfish.json.api.BufferPool;

class JsonReaderFactoryImpl
implements JsonReaderFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonReaderFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonReader createReader(Reader reader) {
        return new JsonReaderImpl(reader, this.bufferPool);
    }

    @Override
    public JsonReader createReader(InputStream in) {
        return new JsonReaderImpl(in, this.bufferPool);
    }

    @Override
    public JsonReader createReader(InputStream in, Charset charset) {
        return new JsonReaderImpl(in, charset, this.bufferPool);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}

