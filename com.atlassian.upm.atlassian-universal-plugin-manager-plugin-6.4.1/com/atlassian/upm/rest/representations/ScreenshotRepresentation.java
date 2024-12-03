/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.net.URI;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ScreenshotRepresentation {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final Integer width;
    @JsonProperty
    private final Integer height;
    @JsonProperty
    private final URI link;
    @JsonProperty
    private final String imageType;
    @JsonProperty
    private final String altText;

    @JsonCreator
    public ScreenshotRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="width") Integer width, @JsonProperty(value="height") Integer height, @JsonProperty(value="link") URI link, @JsonProperty(value="imageType") String imageType, @JsonProperty(value="altText") String altText) {
        this.name = Objects.requireNonNull(name, "name");
        this.width = Objects.requireNonNull(width, "width");
        this.height = Objects.requireNonNull(height, "height");
        this.link = Objects.requireNonNull(link, "link");
        this.imageType = Objects.requireNonNull(imageType, "imageType");
        this.altText = altText;
    }

    public String getName() {
        return this.name;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public URI getLink() {
        return this.link;
    }

    public String getImageType() {
        return this.imageType;
    }

    public String getAltText() {
        return this.altText;
    }
}

