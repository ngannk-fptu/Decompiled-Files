/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;

public interface ContextualKeyDeserializer {
    public KeyDeserializer createContextual(DeserializationConfig var1, BeanProperty var2) throws JsonMappingException;
}

