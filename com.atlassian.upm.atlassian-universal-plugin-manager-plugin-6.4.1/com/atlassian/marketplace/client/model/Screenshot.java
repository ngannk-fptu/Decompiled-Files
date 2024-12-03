/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import io.atlassian.fugue.Option;

public final class Screenshot {
    Links _links;
    @ReadOnly
    ScreenshotEmbedded _embedded;
    Option<String> caption;

    public Links getLinks() {
        return this._links;
    }

    public Option<String> getCaption() {
        return this.caption;
    }

    public ImageInfo getImage() {
        return this._embedded.image;
    }

    static final class ScreenshotEmbedded {
        ImageInfo image;

        ScreenshotEmbedded() {
        }
    }
}

