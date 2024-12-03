/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.app.ConfluenceSpaceContainerV1;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.ContainerV1;
import com.atlassian.migration.app.JiraProjectContainerV1;
import com.atlassian.migration.app.SiteContainerV1;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

public class ContainerV1Deserialiser
implements JsonDeserializer<ContainerV1> {
    private final Gson gson = new Gson();

    public ContainerV1 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        String containerTypeAsString = jsonElement.getAsJsonObject().get("type").getAsString();
        ContainerType containerType = ContainerType.valueOf(containerTypeAsString);
        switch (containerType) {
            case ConfluenceSpace: {
                return (ContainerV1)this.gson.fromJson(jsonElement, ConfluenceSpaceContainerV1.class);
            }
            case JiraProject: {
                return (ContainerV1)this.gson.fromJson(jsonElement, JiraProjectContainerV1.class);
            }
            case Site: {
                return (ContainerV1)this.gson.fromJson(jsonElement, SiteContainerV1.class);
            }
        }
        throw new IllegalArgumentException("Invalid container type: " + (Object)((Object)containerType));
    }
}

