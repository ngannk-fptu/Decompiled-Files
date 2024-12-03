/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Entity {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String mimeType;
    @JsonProperty
    private final String mediaType;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final long size;

    public Entity(@JsonProperty(value="id") String id, @JsonProperty(value="mimeType") String mimeType, @JsonProperty(value="mediaType") String mediaType, @JsonProperty(value="name") String name, @JsonProperty(value="size") long size) {
        this.id = id;
        this.mimeType = mimeType;
        this.mediaType = mediaType;
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return this.id;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }
}

