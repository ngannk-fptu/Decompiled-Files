/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.doclet.generators.resourcedoc;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class JsonOperations {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
}

