/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.ObjectReader
 *  org.codehaus.jackson.map.ObjectWriter
 */
package com.atlassian.crowd.mapper;

import java.io.IOException;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;

public class SimpleObjectMapper<T> {
    private final ObjectReader reader;
    private final ObjectWriter writer;

    public SimpleObjectMapper(Class<T> clz) {
        this(new ObjectMapper(), clz);
    }

    public SimpleObjectMapper(ObjectMapper mapper, Class<T> clz) {
        this.reader = mapper.reader(clz);
        this.writer = mapper.writerWithType(clz);
    }

    public String serialize(T t) throws IOException {
        return this.writer.writeValueAsString(t);
    }

    public T deserialize(@Nullable String syncToken) throws IOException {
        return (T)this.reader.readValue(syncToken);
    }
}

