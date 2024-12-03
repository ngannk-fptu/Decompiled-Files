/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.api;

import com.atlassian.confluence.plugins.files.api.CommentAnchor;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CommentAnchorPin
extends CommentAnchor {
    static final String ANCHOR_TYPE = "pin";
    @JsonProperty
    private final int page;
    @JsonProperty
    private final double x;
    @JsonProperty
    private final double y;

    @JsonCreator
    public CommentAnchorPin(@JsonProperty(value="page") int page, @JsonProperty(value="x") double x, @JsonProperty(value="y") double y) {
        this.page = page;
        this.x = x;
        this.y = y;
    }

    public int getPage() {
        return this.page;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}

