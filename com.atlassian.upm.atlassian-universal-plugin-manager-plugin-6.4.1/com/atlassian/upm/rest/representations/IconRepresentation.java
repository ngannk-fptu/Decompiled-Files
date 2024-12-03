/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.upm.api.util.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class IconRepresentation {
    @JsonProperty
    private final Integer width;
    @JsonProperty
    private final Integer height;
    @JsonProperty
    private final URI link;

    @JsonCreator
    public IconRepresentation(@JsonProperty(value="width") Integer width, @JsonProperty(value="height") Integer height, @JsonProperty(value="link") URI link) {
        this.width = width;
        this.height = height;
        this.link = Objects.requireNonNull(link, "link");
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

    public static IconRepresentation newIcon(Option<ImageInfo> icon) {
        Iterator<ImageInfo> iterator = icon.iterator();
        if (iterator.hasNext()) {
            ImageInfo image = iterator.next();
            if (image == null || image.getImageUri() == null) {
                return null;
            }
            return new IconRepresentation(72, 72, image.getImageUri());
        }
        return null;
    }
}

