/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import org.glassfish.json.JsonGeneratorImpl;
import org.glassfish.json.JsonPrettyGeneratorImpl;
import org.glassfish.json.api.BufferPool;

class JsonGeneratorFactoryImpl
implements JsonGeneratorFactory {
    private final boolean prettyPrinting;
    private final Map<String, ?> config;
    private final BufferPool bufferPool;

    JsonGeneratorFactoryImpl(Map<String, ?> config, boolean prettyPrinting, BufferPool bufferPool) {
        this.config = config;
        this.prettyPrinting = prettyPrinting;
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(writer, this.bufferPool) : new JsonGeneratorImpl(writer, this.bufferPool);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(out, this.bufferPool) : new JsonGeneratorImpl(out, this.bufferPool);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        return this.prettyPrinting ? new JsonPrettyGeneratorImpl(out, charset, this.bufferPool) : new JsonGeneratorImpl(out, charset, this.bufferPool);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}

