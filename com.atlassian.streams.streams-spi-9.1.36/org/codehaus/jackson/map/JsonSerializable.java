/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;

@Deprecated
public interface JsonSerializable {
    public void serialize(JsonGenerator var1, SerializerProvider var2) throws IOException, JsonProcessingException;
}

