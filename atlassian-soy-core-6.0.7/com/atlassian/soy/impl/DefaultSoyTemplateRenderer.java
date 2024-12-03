/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.atlassian.soy.impl;

import com.atlassian.soy.impl.SoyManager;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;

public class DefaultSoyTemplateRenderer
implements SoyTemplateRenderer {
    private final SoyManager soyManager;

    public DefaultSoyTemplateRenderer(SoyManager soyManager) {
        this.soyManager = soyManager;
    }

    public void clearAllCaches() {
        this.soyManager.clearCaches(null);
    }

    public void clearCache(String completeModuleKey) {
        Preconditions.checkNotNull((Object)completeModuleKey, (Object)"completeModuleKey");
        this.soyManager.clearCaches(completeModuleKey);
    }

    public String render(String completeModuleKey, String templateName, Map<String, Object> data) throws SoyException {
        StringBuilder sb = new StringBuilder();
        this.render(sb, completeModuleKey, templateName, data);
        return sb.toString();
    }

    public void render(Appendable appendable, String completeModuleKey, String templateName, Map<String, Object> data) throws SoyException {
        this.render(appendable, completeModuleKey, templateName, data, Maps.newHashMap());
    }

    public void render(Appendable appendable, String completeModuleKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws SoyException {
        try (Ticker ignored = Metrics.metric((String)"webTemplateRenderer").tag("templateRenderer", "soy").tag("templateName", templateName).withAnalytics().fromPluginKey(completeModuleKey.split(":")[0]).startTimer();){
            this.soyManager.render(appendable, completeModuleKey, templateName, data, injectedData);
        }
    }
}

