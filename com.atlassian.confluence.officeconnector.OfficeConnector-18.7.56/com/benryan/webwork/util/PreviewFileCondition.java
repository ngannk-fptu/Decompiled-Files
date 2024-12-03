/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class PreviewFileCondition
implements Condition {
    private static final String[] EXTENSIONS = new String[]{"doc", "docx", "pdf", "ppt", "pptx", "xls", "xlsx"};

    public void init(Map map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map map) {
        Attachment file = (Attachment)map.get("attachment");
        if (file != null) {
            String fileName = file.getFileName().toLowerCase();
            for (String extension : EXTENSIONS) {
                if (!fileName.endsWith("." + extension)) continue;
                return true;
            }
        }
        return false;
    }
}

