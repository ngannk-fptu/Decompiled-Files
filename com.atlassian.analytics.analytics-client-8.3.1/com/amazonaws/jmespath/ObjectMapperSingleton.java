/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class ObjectMapperSingleton {
    private ObjectMapperSingleton() {
    }

    public static ObjectMapper getObjectMapper() {
        return InstanceHolder.OBJECT_MAPPER;
    }

    private static final class InstanceHolder {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        private InstanceHolder() {
        }
    }
}

