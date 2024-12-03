/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.web;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import java.util.List;
import java.util.Map;

public interface WebFragmentHelper {
    public Condition loadCondition(String var1, Plugin var2) throws ConditionLoadingException;

    public ContextProvider loadContextProvider(String var1, Plugin var2) throws ConditionLoadingException;

    public String getI18nValue(String var1, List<?> var2, Map<String, Object> var3);

    public String renderVelocityFragment(String var1, Map<String, Object> var2);
}

