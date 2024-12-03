/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ClientId {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String description;

    public ClientId(String id, String key) {
        this(id, key, null, null);
    }

    public ClientId(@JsonProperty(value="id") String id, @JsonProperty(value="key") String key, @Nullable @JsonProperty(value="title") String title, @Nullable @JsonProperty(value="description") String description) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }
}

