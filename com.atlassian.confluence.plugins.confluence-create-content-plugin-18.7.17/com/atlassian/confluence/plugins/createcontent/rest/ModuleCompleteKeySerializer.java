/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.plugin.ModuleCompleteKey;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ModuleCompleteKeySerializer
extends JsonSerializer<ModuleCompleteKey> {
    public void serialize(ModuleCompleteKey value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getCompleteKey());
    }
}

