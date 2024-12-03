/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.schema;

import java.lang.reflect.Type;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;

public interface SchemaAware {
    public JsonNode getSchema(SerializerProvider var1, Type var2) throws JsonMappingException;
}

