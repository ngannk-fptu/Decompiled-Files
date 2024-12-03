/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.linktypes.AbstractContentEntityLink;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.links.GenericLinkParser;
import java.util.Arrays;

public class SpaceLink
extends AbstractContentEntityLink {
    private Space space;
    private String spaceKey;
    private ContentEntityObject destinationContent;

    public SpaceLink(GenericLinkParser parser, SpaceManager spaceManager) {
        super(parser);
        this.relativeUrl = true;
        this.spaceKey = parser.getSpaceKey();
        this.url = "/display/" + this.spaceKey;
        this.setI18nTitle("renderer.view.space", Arrays.asList(this.spaceKey));
        this.space = spaceManager.getSpace(this.spaceKey);
        if (this.space != null) {
            this.setI18nTitle("renderer.view.space", Arrays.asList(this.space.getName()));
        }
    }

    @Override
    public ContentEntityObject getDestinationContent() {
        if (this.destinationContent == null && this.space != null) {
            this.destinationContent = this.space.getHomePage() != null ? this.space.getHomePage() : this.space.getDescription();
        }
        return this.destinationContent;
    }

    @Override
    public String getLinkBody() {
        if (this.isNoLinkBodyProvided() && this.space != null) {
            return this.space.getName();
        }
        return super.getLinkBody();
    }

    @Override
    public boolean hasDestination() {
        return this.space != null;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

