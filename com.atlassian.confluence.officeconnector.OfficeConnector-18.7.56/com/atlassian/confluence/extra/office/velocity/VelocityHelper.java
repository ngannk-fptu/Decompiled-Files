/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.extra.office.velocity;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.benryan.conversion.WebDavUtil;

public class VelocityHelper {
    public String getWebDavUrl(Attachment attachment) {
        WebDavUtil webDavUtil = new WebDavUtil((AbstractPage)attachment.getContainer());
        return webDavUtil.getRelWebDavUrl(attachment.getFileName());
    }
}

