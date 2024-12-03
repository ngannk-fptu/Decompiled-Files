/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.rest.serialization.SerializerModule;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;

public class CustomSerializerModuleFactory {
    public static Module create() {
        return SerializerModule.INSTANCE;
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(CustomSerializerModuleFactory.create());
        return mapper;
    }
}

