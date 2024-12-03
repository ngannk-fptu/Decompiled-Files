/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.web.UrlBuilder
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.web.UrlBuilder;
import java.util.Map;

public interface JiraChart {
    public Map<String, Object> setupContext(Map<String, String> var1, JQLValidationResult var2, ConversionContext var3) throws MacroExecutionException;

    @Deprecated
    public String getImagePlaceholderUrl(Map<String, String> var1, UrlBuilder var2);

    public String getDefaultImagePlaceholderUrl();

    public String getJiraGadgetRestUrl();

    public String getTemplateFileName();

    public String[] getChartParameters();

    public boolean isVerifyChartSupported();
}

