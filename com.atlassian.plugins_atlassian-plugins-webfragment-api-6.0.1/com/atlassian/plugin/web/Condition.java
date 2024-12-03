/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.plugin.web;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.baseconditions.BaseCondition;
import java.util.Map;

public interface Condition
extends BaseCondition {
    public void init(Map<String, String> var1) throws PluginParseException;

    public boolean shouldDisplay(Map<String, Object> var1);
}

