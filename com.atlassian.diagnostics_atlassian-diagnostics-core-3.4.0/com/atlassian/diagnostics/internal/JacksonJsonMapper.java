/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.detail.ThreadDump
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.module.SimpleModule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.detail.ThreadDump;
import com.atlassian.diagnostics.internal.detail.SimpleThreadDump;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonJsonMapper<T>
implements JsonMapper<T> {
    private static final Logger log = LoggerFactory.getLogger(JacksonJsonMapper.class);
    private final Class<T> type;
    protected final ObjectMapper objectMapper;

    public JacksonJsonMapper(Class<T> type) {
        this.type = type;
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("Atlassian Diagnostics Built-in", new Version(1, 0, 0, null));
        module.addDeserializer(ThreadDump.class, (JsonDeserializer)new JsonDeserializer<ThreadDump>(){

            public ThreadDump deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                return (ThreadDump)jp.readValueAs(SimpleThreadDump.class);
            }
        });
        this.objectMapper.registerModule((Module)module);
    }

    @Nonnull
    public Class<T> getType() {
        return this.type;
    }

    public T parseJson(String json) {
        if (StringUtils.isBlank((CharSequence)json)) {
            return null;
        }
        try {
            return (T)this.objectMapper.readValue(json, this.type);
        }
        catch (IOException e) {
            log.warn("Failed to parse json as {}: {}", new Object[]{this.type.getName(), e.getMessage(), log.isDebugEnabled() ? e : null});
            return null;
        }
    }

    public String toJson(T value) {
        if (value == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = this.objectMapper.getJsonFactory().createJsonGenerator((Writer)writer);){
            generator.writeObject(value);
            generator.flush();
        }
        catch (IOException e) {
            log.warn("Unexpected exception while rendering an event object to a JSON string", (Throwable)e);
        }
        return writer.toString();
    }
}

