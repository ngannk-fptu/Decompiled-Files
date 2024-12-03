/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  com.atlassian.cmpt.domain.Product
 *  com.atlassian.migration.json.EditionDeserializer
 *  com.atlassian.migration.json.EditionSerializer
 *  com.atlassian.migration.json.InstantDeserializer
 *  com.atlassian.migration.json.InstantSerializer
 *  com.atlassian.migration.json.LocalDateDeserializer
 *  com.atlassian.migration.json.LocalDateSerializer
 *  com.atlassian.migration.json.LocalTimeDeserializer
 *  com.atlassian.migration.json.LocalTimeSerializer
 *  com.atlassian.migration.json.ProductDeserializer
 *  com.atlassian.migration.json.ProductSerializer
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.module.SimpleModule
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.migration.agent.json;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.cmpt.domain.Product;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.json.EditionDeserializer;
import com.atlassian.migration.json.EditionSerializer;
import com.atlassian.migration.json.InstantDeserializer;
import com.atlassian.migration.json.InstantSerializer;
import com.atlassian.migration.json.LocalDateDeserializer;
import com.atlassian.migration.json.LocalDateSerializer;
import com.atlassian.migration.json.LocalTimeDeserializer;
import com.atlassian.migration.json.LocalTimeSerializer;
import com.atlassian.migration.json.ProductDeserializer;
import com.atlassian.migration.json.ProductSerializer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;

public class Jsons {
    public static final ObjectMapper OBJECT_MAPPER = Jsons.initialize();

    private Jsons() {
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return (T)OBJECT_MAPPER.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new JsonSerializingException("Failed to read a value from a JSON string", e);
        }
    }

    public static <T> T readValue(String json, TypeReference typeRef) {
        try {
            return (T)OBJECT_MAPPER.readValue(json, typeRef);
        }
        catch (IOException e) {
            throw new JsonSerializingException("Failed to read a value from a JSON string", e);
        }
    }

    public static <T> T readValue(File src, TypeReference typeRef) throws IOException {
        return (T)OBJECT_MAPPER.readValue(src, typeRef);
    }

    public static <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return (T)OBJECT_MAPPER.readValue(src, valueType);
    }

    public static <T> T readValue(InputStream src, TypeReference typeRef) throws IOException {
        return (T)OBJECT_MAPPER.readValue(src, typeRef);
    }

    public static String valueAsString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        }
        catch (IOException e) {
            throw new JsonSerializingException("Failed to write a value to a JSON string", e);
        }
    }

    public static void valueAsJsonFile(File resultFile, Object value) throws IOException {
        OBJECT_MAPPER.writeValue(resultFile, value);
    }

    private static ObjectMapper initialize() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(Jsons.createModule());
        return mapper;
    }

    public static Module createModule() {
        return new SimpleModule("Migration Agent", Version.unknownVersion()).addSerializer(Instant.class, (JsonSerializer)new InstantSerializer()).addSerializer(LocalDate.class, (JsonSerializer)new LocalDateSerializer()).addSerializer(LocalTime.class, (JsonSerializer)new LocalTimeSerializer()).addSerializer(Edition.class, (JsonSerializer)new EditionSerializer()).addSerializer(Product.class, (JsonSerializer)new ProductSerializer()).addDeserializer(Instant.class, (JsonDeserializer)new InstantDeserializer()).addDeserializer(LocalDate.class, (JsonDeserializer)new LocalDateDeserializer()).addDeserializer(LocalTime.class, (JsonDeserializer)new LocalTimeDeserializer()).addDeserializer(Edition.class, (JsonDeserializer)new EditionDeserializer()).addDeserializer(Product.class, (JsonDeserializer)new ProductDeserializer());
    }
}

