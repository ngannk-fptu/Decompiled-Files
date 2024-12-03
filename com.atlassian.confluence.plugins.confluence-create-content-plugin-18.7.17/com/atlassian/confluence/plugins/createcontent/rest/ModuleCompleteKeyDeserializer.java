/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.plugin.ModuleCompleteKey;
import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ModuleCompleteKeyDeserializer
extends JsonDeserializer<ModuleCompleteKey> {
    public ModuleCompleteKey deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return new ModuleCompleteKey(jp.getText());
    }
}

