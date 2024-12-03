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

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.atlassian.migration.agent.service.catalogue.model.AppContainer;
import com.atlassian.migration.agent.service.catalogue.model.ConfluenceSpaceContainer;
import com.atlassian.migration.agent.service.catalogue.model.SiteContainer;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

public class AbstractContainerDeserialiser
implements JsonDeserializer<AbstractContainer> {
    private final Gson gson = new Gson();

    public AbstractContainer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        String containerTypeAsString = jsonElement.getAsJsonObject().get("type").getAsString();
        AbstractContainer.Type containerType = AbstractContainer.Type.valueOf(containerTypeAsString);
        switch (containerType) {
            case ConfluenceSpace: {
                return (AbstractContainer)this.gson.fromJson(jsonElement, ConfluenceSpaceContainer.class);
            }
            case Site: {
                return (AbstractContainer)this.gson.fromJson(jsonElement, SiteContainer.class);
            }
            case App: {
                return (AbstractContainer)this.gson.fromJson(jsonElement, AppContainer.class);
            }
        }
        throw new IllegalArgumentException("Invalid container type: " + (Object)((Object)containerType));
    }
}

