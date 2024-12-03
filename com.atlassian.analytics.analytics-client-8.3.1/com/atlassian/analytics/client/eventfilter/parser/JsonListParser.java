/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.deser.std.StringDeserializer
 *  org.codehaus.jackson.map.module.SimpleModule
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.eventfilter.parser;

import com.atlassian.analytics.client.eventfilter.reader.FilterListReader;
import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StringDeserializer;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonListParser {
    private static final Logger LOG = LoggerFactory.getLogger(JsonListParser.class);
    private final FilterListReader filterListReader;

    public JsonListParser(FilterListReader filterListReader) {
        this.filterListReader = filterListReader;
    }

    public Map<String, FilteredEventAttributes> readJsonFilterList(String filterListName) {
        try {
            return this.readJsonFilterListAndFailOnError(filterListName);
        }
        catch (IOException e) {
            LOG.error("Couldn't read the JSON list {} with error: ", (Object)filterListName, (Object)e);
            return null;
        }
    }

    public Map<String, FilteredEventAttributes> readJsonFilterListAndFailOnError(String filterListName) throws IOException {
        ObjectMapper mapper = this.createObjectMapper();
        InputStream listInputStream = this.filterListReader.readFilterList(filterListName);
        if (listInputStream != null) {
            return (Map)mapper.readValue(listInputStream, (TypeReference)new TypeReference<Map<String, FilteredEventAttributes>>(){});
        }
        throw new IOException("Couldn't find and read the JSON list " + filterListName);
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("StringMappingModule", new Version(1, 0, 0, null));
        module.addDeserializer(String.class, (JsonDeserializer)new NonForgivingStringDeserializer());
        mapper.registerModule((Module)module);
        return mapper;
    }

    private static class NonForgivingStringDeserializer
    extends JsonDeserializer<String> {
        final StringDeserializer stringDeserializer = new StringDeserializer();

        private NonForgivingStringDeserializer() {
        }

        public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return this.stringDeserializer.deserialize(jp, ctxt);
        }
    }
}

