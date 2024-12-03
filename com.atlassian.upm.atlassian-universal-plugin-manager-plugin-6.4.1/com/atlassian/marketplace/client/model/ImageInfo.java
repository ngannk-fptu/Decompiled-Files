/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Link;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;

public final class ImageInfo
implements Entity {
    Links _links;
    @RequiredLink(rel="self")
    URI selfUri;
    @RequiredLink(rel="image")
    URI imageUri;

    @Override
    public Links getLinks() {
        return this._links;
    }

    public URI getImageUri() {
        return this.imageUri;
    }

    public Option<URI> getImageUri(Size size, Resolution resolution) {
        return this._links.getUri(ImageInfo.getImageLinkRel(size, resolution));
    }

    public Option<String> getImageContentType(Size size, Resolution resolution) {
        Iterator iterator = this._links.getLink(ImageInfo.getImageLinkRel(size, resolution)).iterator();
        if (iterator.hasNext()) {
            Link link = (Link)iterator.next();
            return link.getType();
        }
        return Option.none();
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    static String getImageLinkRel(Size size, Resolution resolution) {
        if (resolution == Resolution.HIGH_RESOLUTION) {
            return size == Size.SMALL_SIZE ? "smallHighResImage" : "highRes";
        }
        return size == Size.SMALL_SIZE ? "smallImage" : "image";
    }

    public static enum Resolution {
        DEFAULT_RESOLUTION,
        HIGH_RESOLUTION;

    }

    public static enum Size {
        DEFAULT_SIZE,
        SMALL_SIZE;

    }
}

