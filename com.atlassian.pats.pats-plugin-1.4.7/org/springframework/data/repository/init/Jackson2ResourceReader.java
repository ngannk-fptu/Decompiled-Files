/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.init;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class Jackson2ResourceReader
implements ResourceReader {
    private static final String DEFAULT_TYPE_KEY = "_class";
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    private final ObjectMapper mapper;
    private String typeKey = "_class";

    public Jackson2ResourceReader() {
        this(DEFAULT_MAPPER);
    }

    public Jackson2ResourceReader(@Nullable ObjectMapper mapper) {
        this.mapper = mapper == null ? DEFAULT_MAPPER : mapper;
    }

    public void setTypeKey(@Nullable String typeKey) {
        this.typeKey = typeKey == null ? DEFAULT_TYPE_KEY : typeKey;
    }

    @Override
    public Object readFrom(Resource resource, @Nullable ClassLoader classLoader) throws Exception {
        Assert.notNull((Object)resource, (String)"Resource must not be null!");
        InputStream stream = resource.getInputStream();
        JsonNode node = this.mapper.readerFor(JsonNode.class).readTree(stream);
        if (node.isArray()) {
            Iterator elements = node.elements();
            ArrayList<Object> result = new ArrayList<Object>();
            while (elements.hasNext()) {
                JsonNode element = (JsonNode)elements.next();
                result.add(this.readSingle(element, classLoader));
            }
            return result;
        }
        return this.readSingle(node, classLoader);
    }

    private Object readSingle(JsonNode node, @Nullable ClassLoader classLoader) throws IOException {
        JsonNode typeNode = node.findValue(this.typeKey);
        if (typeNode == null) {
            throw new IllegalArgumentException(String.format("Could not find type for type key '%s'!", this.typeKey));
        }
        String typeName = typeNode.asText();
        Class type = ClassUtils.resolveClassName((String)typeName, (ClassLoader)classLoader);
        return this.mapper.readerFor(type).readValue(node);
    }

    static {
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

