/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.office.OfficeFile
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.extra.office.webwork;

import com.atlassian.confluence.extra.office.OfficeFile;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class IsOfficeFileAttachment
implements Condition {
    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Attachment attachment = (Attachment)context.get("attachment");
        if (attachment != null) {
            return OfficeFile.isOfficeExtension((String)attachment.getFileExtension()) || OfficeFile.isOfficeMimeType((String)attachment.getMediaType());
        }
        return false;
    }
}

