/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.ImmutablePair
 */
package com.atlassian.confluence.plugins.createcontent.contextproviders;

import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class CreateDialogInitParamsContextProvider
implements ContextProvider {
    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        Map parameterMap = (Map)FlashScope.get((String)"createDialogInitParams");
        if (parameterMap == null) {
            return context;
        }
        ArrayList params = Lists.newArrayList();
        for (Map.Entry entry : parameterMap.entrySet()) {
            params.add(new ImmutablePair((Object)((String)entry.getKey()), (Object)StringUtils.join((Object[])((Object[])entry.getValue()), (char)',')));
        }
        context.put("params", params);
        return context;
    }
}

