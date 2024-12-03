/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.troubleshooting.cluster;

import com.atlassian.troubleshooting.cluster.JsonSerialiser;
import com.atlassian.troubleshooting.stp.util.ObjectMapperFactory;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.map.ObjectMapper;

@ParametersAreNonnullByDefault
public class DefaultJsonSerialiser
implements JsonSerialiser {
    final ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return (T)this.mapper.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading " + json + " from JSON", e);
        }
    }

    @Override
    public String toJson(Object o) {
        try {
            return this.mapper.writeValueAsString(o);
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing " + o + " to JSON", e);
        }
    }
}

