/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.email.soy.AttachmentMediaTypeConverter;
import com.atlassian.confluence.plugins.email.soy.BaseAttachmentFunction;

public class GetAttachmentFileIconFunction
extends BaseAttachmentFunction<String> {
    public GetAttachmentFileIconFunction(AttachmentManager attachmentManager) {
        super(attachmentManager);
    }

    @Override
    protected String applyTo(Attachment attachment) {
        return AttachmentMediaTypeConverter.getIconResourceName(attachment);
    }

    public String getName() {
        return "getAttachmentIcon";
    }
}

