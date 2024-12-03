/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.type.JavaType;

public interface KeyDeserializers {
    public KeyDeserializer findKeyDeserializer(JavaType var1, DeserializationConfig var2, BeanDescription var3, BeanProperty var4) throws JsonMappingException;
}

