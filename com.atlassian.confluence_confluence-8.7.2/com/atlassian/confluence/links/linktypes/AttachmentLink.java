/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.AbstractAttachmentLink;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.renderer.links.GenericLinkParser;
import java.util.Arrays;

public class AttachmentLink
extends AbstractAttachmentLink {
    public static final String ATTACHMENT_ICON = "attachment";
    private AbstractPageLink abstractPageLink;

    public AttachmentLink(GenericLinkParser parser, AbstractPageLink abstractPageLink, AttachmentManager attachmentManager) {
        super(parser);
        this.relativeUrl = true;
        this.abstractPageLink = abstractPageLink;
        ContentEntityObject destinationContent = abstractPageLink.getDestinationContent();
        if (destinationContent != null) {
            this.attachment = attachmentManager.getAttachment(destinationContent, parser.getAttachmentName());
        }
        if (destinationContent != null && this.attachment != null) {
            this.url = this.attachment.getDownloadPath();
            this.setI18nTitle("renderer.attached.to", Arrays.asList(this.attachment.getFileName(), destinationContent.getTitle()));
            this.iconName = ATTACHMENT_ICON;
        }
        if (this.linkBody.startsWith("^") && this.linkBody.length() > 1) {
            this.linkBody = this.linkBody.substring(1);
        }
    }

    public AbstractPageLink getAbstractPageLink() {
        return this.abstractPageLink;
    }
}

