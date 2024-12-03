/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import org.glassfish.json.JsonWriterImpl;
import org.glassfish.json.api.BufferPool;

class JsonWriterFactoryImpl
implements JsonWriterFactory {
    private final Map<String, ?> config;
    private final boolean prettyPrinting;
    private final BufferPool bufferPool;

    JsonWriterFactoryImpl(Map<String, ?> config, boolean prettyPrinting, BufferPool bufferPool) {
        this.config = config;
        this.prettyPrinting = prettyPrinting;
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new JsonWriterImpl(writer, this.prettyPrinting, this.bufferPool);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return new JsonWriterImpl(out, this.prettyPrinting, this.bufferPool);
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return new JsonWriterImpl(out, charset, this.prettyPrinting, this.bufferPool);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}

