/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.audit.frontend.contextproviders;

import com.atlassian.audit.frontend.util.URLEncoderUtil;
import com.atlassian.plugin.web.ContextProvider;
import java.util.HashMap;
import java.util.Map;

public class UrlEncoderContextProvider
implements ContextProvider {
    public Map<String, Object> getContextMap(Map<String, Object> context) {
        HashMap<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("urlEncoder", new URLEncoderUtil());
        return contextMap;
    }

    public void init(Map<String, String> params) {
    }
}

