/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.themes.GlobalHelper;

public class AttachmentHelper
extends GlobalHelper {
    private Attachment attachment;

    public AttachmentHelper(ConfluenceActionSupport action, Attachment attachment) {
        super(action);
        this.attachment = attachment;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }
}

