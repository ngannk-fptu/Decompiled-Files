/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.stream.JsonWriter
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.DefaultMapping;
import com.atlassian.plugin.webresource.cdn.mapper.DefaultMappingSet;
import com.atlassian.plugin.webresource.cdn.mapper.Mapping;
import com.atlassian.plugin.webresource.cdn.mapper.MappingParserException;
import com.atlassian.plugin.webresource.cdn.mapper.MappingSet;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingParser {
    private static final Logger log = LoggerFactory.getLogger(MappingParser.class);

    @Nonnull
    public MappingSet parse(@Nonnull Reader mappingReader) throws MappingParserException {
        Preconditions.checkNotNull((Object)mappingReader, (Object)"Can't read from null reader!");
        try {
            JsonElement mappingRoot = new JsonParser().parse(mappingReader);
            JsonObject mappings = mappingRoot.getAsJsonObject();
            if (mappings == null) {
                throw new MappingParserException("Root object 'mappings' not found in JSON!");
            }
            ArrayList<Mapping> mappedResources = new ArrayList<Mapping>();
            for (Map.Entry item : mappings.entrySet()) {
                Stream<String> values = StreamSupport.stream(((JsonElement)item.getValue()).getAsJsonArray().spliterator(), false).map(JsonElement::getAsString);
                mappedResources.add(new DefaultMapping((String)item.getKey(), values));
            }
            log.debug("MappingParser just read {} entries.", (Object)mappedResources.size());
            return new DefaultMappingSet(mappedResources);
        }
        catch (JsonIOException | JsonSyntaxException | IllegalStateException e) {
            log.error("Failed to read mappings!", e);
            throw new MappingParserException("Failed to read mappings!", e);
        }
    }

    @Nonnull
    public String getAsString(@Nonnull MappingSet mappings) throws IOException {
        Preconditions.checkNotNull((Object)mappings, (Object)"Can't write null mappings!");
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter((Writer)stringWriter);
            jsonWriter.setLenient(true);
            jsonWriter.beginObject();
            for (Mapping mapping : mappings.all()) {
                jsonWriter.name(mapping.originalResource());
                jsonWriter.beginArray();
                for (String mappedResource : mapping.mappedResources()) {
                    jsonWriter.value(mappedResource);
                }
                jsonWriter.endArray();
            }
            jsonWriter.endObject();
            return stringWriter.toString();
        }
        catch (IOException e) {
            log.error("Failed to serialize MappingSet to JSON!", (Throwable)e);
            throw e;
        }
    }
}

