/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.linktypes.AbstractContentEntityLink;
import com.atlassian.renderer.links.GenericLinkParser;

public class ContentLink
extends AbstractContentEntityLink {
    private ContentEntityObject content;
    private String unpermittedLinkBody;

    public ContentLink(GenericLinkParser parser, ContentEntityManager contentEntityManager) {
        super(parser);
        this.content = contentEntityManager.getById(parser.getContentId());
        if (this.content != null) {
            this.setUrlAndTitleFromContent(parser);
        }
    }

    @Override
    public ContentEntityObject getDestinationContent() {
        return this.content;
    }

    public String getUnpermittedLinkBody() {
        return this.unpermittedLinkBody;
    }

    private void setUrlAndTitleFromContent(GenericLinkParser parser) {
        this.url = this.content.getUrlPath();
        this.setTitle(this.content.getDisplayTitle());
        this.unpermittedLinkBody = this.linkBody;
        if (parser.getLinkBody() == null) {
            this.linkBody = this.content.getDisplayTitle();
        }
    }
}

