/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.upm.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.upm.core.Sys;
import java.util.Map;

public class IsDevMode
implements Condition {
    public void init(Map<String, String> paramMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return Sys.isDevModeEnabled();
    }
}

