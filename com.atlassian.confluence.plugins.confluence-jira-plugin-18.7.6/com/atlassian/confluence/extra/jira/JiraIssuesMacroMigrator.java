/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Map;
import java.util.Set;

public class JiraIssuesMacroMigrator
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        Map parameters = macro.getParameters();
        if (parameters != null) {
            Set keySet = parameters.keySet();
            for (String key : keySet) {
                if (!key.startsWith("http://") && !key.startsWith("https://")) continue;
                String val = (String)parameters.remove(key);
                String url = key + "=" + val;
                parameters.put("url", url);
                break;
            }
        }
        return macro;
    }
}

