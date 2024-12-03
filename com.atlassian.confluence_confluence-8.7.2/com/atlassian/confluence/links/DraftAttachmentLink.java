/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.links.AbstractAttachmentLink;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.renderer.links.GenericLinkParser;
import java.util.Arrays;

public class DraftAttachmentLink
extends AbstractAttachmentLink {
    public DraftAttachmentLink(GenericLinkParser parser) {
        super(parser);
    }

    public DraftAttachmentLink(GenericLinkParser parser, Draft draft, AttachmentManager attachmentManager) {
        super(parser);
        this.relativeUrl = true;
        this.attachment = attachmentManager.getAttachment(draft, parser.getAttachmentName());
        if (this.attachment != null) {
            this.url = this.attachment.getDownloadPath();
            this.setI18nTitle("renderer.attached.to", Arrays.asList(this.attachment.getFileName(), draft.getTitle()));
            this.iconName = "attachment";
        }
        if (this.linkBody.startsWith("^") && this.linkBody.length() > 1) {
            this.linkBody = this.linkBody.substring(1);
        }
    }
}

