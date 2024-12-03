/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util.json;

import com.amazonaws.SdkClientException;
import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public enum Jackson {

    private static final InternalLogApi log = InternalLogFactory.getLog(Jackson.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectWriter writer;
    private static final ObjectWriter prettyWriter;
    private static final TypeReference<HashMap<String, String>> STRING_MAP_TYPEREFERENCE;

    public static String toJsonPrettyString(Object value) {
        try {
            return prettyWriter.writeValueAsString(value);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String toJsonString(Object value) {
        try {
            return writer.writeValueAsString(value);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to parse Json String.", e);
        }
    }

    public static Map<String, String> stringMapFromJsonString(String json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, STRING_MAP_TYPEREFERENCE);
        }
        catch (IOException e) {
            throw new SdkClientException("Unable to parse Json String.", e);
        }
    }

    public static <T> T fromSensitiveJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        }
        catch (IOException e) {
            log.debug("Failed to parse JSON string.", e);
            throw new SdkClientException("Unable to parse Json string. See debug-level logs for the exact error details, which may include sensitive information.");
        }
    }

    public static JsonNode jsonNodeOf(String json) {
        return Jackson.fromJsonString(json, JsonNode.class);
    }

    public static JsonGenerator jsonGeneratorOf(Writer writer) throws IOException {
        return new JsonFactory().createGenerator(writer);
    }

    public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(file, clazz);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectWriter getWriter() {
        return writer;
    }

    public static ObjectWriter getPrettywriter() {
        return prettyWriter;
    }

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        writer = objectMapper.writer();
        prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();
        STRING_MAP_TYPEREFERENCE = new TypeReference<HashMap<String, String>>(){};
    }
}

