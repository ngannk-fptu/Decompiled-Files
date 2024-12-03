/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class AttachmentCountContextProvider
implements ContextProvider {
    private AttachmentManager attachmentManager;

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        Object ceoObject = context.get("page");
        if (!(ceoObject instanceof ContentEntityObject)) {
            return context;
        }
        ContentEntityObject ceo = (ContentEntityObject)ceoObject;
        context.put("numAttachments", this.attachmentManager.countLatestVersionsOfAttachments(ceo));
        return context;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

