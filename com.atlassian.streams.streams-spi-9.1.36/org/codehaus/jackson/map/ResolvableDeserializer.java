/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonMappingException;

public interface ResolvableDeserializer {
    public void resolve(DeserializationConfig var1, DeserializerProvider var2) throws JsonMappingException;
}

