/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.json.internal.JsonSchemaHelper;
import com.hazelcast.json.internal.JsonSchemaNode;
import com.hazelcast.map.impl.MetadataInitializer;
import com.hazelcast.nio.serialization.Data;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JsonMetadataInitializer
implements MetadataInitializer {
    public static final JsonMetadataInitializer INSTANCE = new JsonMetadataInitializer();
    private static final int UTF_CHAR_COUNT_FIELD_SIZE = 4;
    private static final JsonFactory FACTORY = new JsonFactory();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object createFromData(Data data) throws IOException {
        if (data.isJson()) {
            try (JsonParser parser = FACTORY.createParser(new ByteArrayInputStream(data.toByteArray(), 12, data.dataSize() - 4));){
                JsonSchemaNode jsonSchemaNode = JsonSchemaHelper.createSchema(parser);
                return jsonSchemaNode;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object createFromObject(Object obj) throws IOException {
        if (obj instanceof HazelcastJsonValue) {
            String str = obj.toString();
            try (JsonParser parser = FACTORY.createParser(str);){
                JsonSchemaNode jsonSchemaNode = JsonSchemaHelper.createSchema(parser);
                return jsonSchemaNode;
            }
        }
        return null;
    }
}

