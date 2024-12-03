/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.net.URI;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HighlightRepresentation {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String text;
    @JsonProperty
    private final String imageTitle;
    @JsonProperty
    private final URI fullImageUri;
    @JsonProperty
    private final URI thumbnailImageUri;

    @JsonCreator
    public HighlightRepresentation(@JsonProperty(value="title") String title, @JsonProperty(value="text") String text, @JsonProperty(value="imageTitle") String imageTitle, @JsonProperty(value="fullImageUri") URI fullImageUri, @JsonProperty(value="thumbnailImageUri") URI thumbnailImageUri) {
        this.title = title;
        this.text = text;
        this.imageTitle = imageTitle;
        this.fullImageUri = fullImageUri;
        this.thumbnailImageUri = thumbnailImageUri;
    }

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public String getImageTitle() {
        return this.imageTitle;
    }

    public URI getFullImageUri() {
        return this.fullImageUri;
    }

    public URI getThumbnailImageUri() {
        return this.thumbnailImageUri;
    }
}

