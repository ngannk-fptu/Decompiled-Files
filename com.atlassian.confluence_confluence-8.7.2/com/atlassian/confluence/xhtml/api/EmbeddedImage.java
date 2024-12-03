/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.StandardTag;

public interface EmbeddedImage
extends StandardTag {
    public ResourceIdentifier getResourceIdentifier();

    public String getMimeType();

    public String getSource();

    public String getAlternativeText();

    public String getHeight();

    public String getWidth();

    public boolean isThumbnail();

    public boolean isBorder();

    public String getAlignment();

    public String getHspace();

    public String getVspace();

    public String getExtraQueryParameters();
}

