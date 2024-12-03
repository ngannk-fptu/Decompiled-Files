/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.vcache.internal.JsonableFactory
 *  com.atlassian.vcache.internal.RequestMetrics
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonMethod
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.vcache.internal.JsonableFactory;
import com.atlassian.vcache.internal.RequestMetrics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonJsonableFactory
implements JsonableFactory {
    private static final ObjectMapper OBJECT_MAPPER;

    public Jsonable apply(RequestMetrics requestMetrics) {
        return writer -> OBJECT_MAPPER.writeValue(writer, (Object)requestMetrics);
    }

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER = mapper;
    }
}

