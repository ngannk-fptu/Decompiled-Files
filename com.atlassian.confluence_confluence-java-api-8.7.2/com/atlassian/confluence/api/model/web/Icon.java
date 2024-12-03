/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@CustomSoyDataMapper(value="jackson2soy")
public class Icon {
    @JsonProperty
    private final String path;
    @JsonProperty
    private final int width;
    @JsonProperty
    private final int height;
    @JsonProperty
    private final boolean isDefault;

    @JsonCreator
    public Icon(@JsonProperty(value="path") String path, @JsonProperty(value="width") int width, @JsonProperty(value="height") int height, @JsonProperty(value="isDefault") boolean isDefault) {
        this.path = path;
        this.width = width;
        this.height = height;
        this.isDefault = isDefault;
    }

    public String getPath() {
        return this.path;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }
}

