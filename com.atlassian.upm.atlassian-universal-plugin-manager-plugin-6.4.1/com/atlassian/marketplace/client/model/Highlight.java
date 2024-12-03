/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import io.atlassian.fugue.Option;

public final class Highlight {
    Links _links;
    @ReadOnly
    Embedded _embedded;
    String title;
    HtmlString body;
    Option<String> explanation;

    public Links getLinks() {
        return this._links;
    }

    public String getTitle() {
        return this.title;
    }

    public HtmlString getBody() {
        return this.body;
    }

    public Option<String> getExplanation() {
        return this.explanation;
    }

    public ImageInfo getFullImage() {
        return this._embedded.screenshot;
    }

    public ImageInfo getThumbnailImage() {
        return this._embedded.thumbnail;
    }

    static final class Embedded {
        ImageInfo screenshot;
        ImageInfo thumbnail;

        Embedded() {
        }
    }
}

