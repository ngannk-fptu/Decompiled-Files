/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.glassfish.json.JsonParserImpl;
import org.glassfish.json.JsonStructureParser;
import org.glassfish.json.api.BufferPool;

class JsonParserFactoryImpl
implements JsonParserFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonParserFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonParser createParser(Reader reader) {
        return new JsonParserImpl(reader, this.bufferPool);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        return new JsonParserImpl(in, this.bufferPool);
    }

    @Override
    public JsonParser createParser(InputStream in, Charset charset) {
        return new JsonParserImpl(in, charset, this.bufferPool);
    }

    @Override
    public JsonParser createParser(JsonArray array) {
        return new JsonStructureParser(array);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }

    @Override
    public JsonParser createParser(JsonObject object) {
        return new JsonStructureParser(object);
    }
}

