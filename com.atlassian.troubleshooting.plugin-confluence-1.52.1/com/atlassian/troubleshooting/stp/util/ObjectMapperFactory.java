/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.troubleshooting.stp.util;

import org.codehaus.jackson.map.ObjectMapper;

public final class ObjectMapperFactory {
    private ObjectMapperFactory() {
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}

