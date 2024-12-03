/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.JsonMappingException;

public class RuntimeJsonMappingException
extends RuntimeException {
    public RuntimeJsonMappingException(JsonMappingException cause) {
        super((Throwable)((Object)cause));
    }

    public RuntimeJsonMappingException(String message) {
        super(message);
    }

    public RuntimeJsonMappingException(String message, JsonMappingException cause) {
        super(message, (Throwable)((Object)cause));
    }
}

