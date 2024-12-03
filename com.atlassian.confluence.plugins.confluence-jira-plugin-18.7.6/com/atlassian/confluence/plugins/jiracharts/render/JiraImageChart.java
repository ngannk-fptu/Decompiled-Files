/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jiracharts.Base64JiraChartImageService;
import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.model.JiraImageChartModel;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChart;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.sal.api.net.ResponseException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JiraImageChart
implements JiraChart {
    private static Logger log = LoggerFactory.getLogger(JiraImageChart.class);
    public static final String SOURCE_IMAGE_PARAM = "srcImg";
    protected ContextPathHolder pathHolder;
    protected Base64JiraChartImageService base64JiraChartImageService;

    public abstract String getJiraGadgetUrl(HttpServletRequest var1);

    public abstract String getDefaultPDFChartWidth();

    protected JiraImageChartModel getImageSourceModel(Map<String, String> parameters, String outputType) throws MacroExecutionException {
        try {
            String width = parameters.get("width");
            if ("pdf".equals(outputType) && StringUtils.isBlank((CharSequence)width)) {
                width = this.getDefaultPDFChartWidth();
            }
            UrlBuilder urlBuilder = JiraChartHelper.getCommonJiraGadgetUrl(parameters.get("jql"), width, this.getJiraGadgetRestUrl());
            JiraChartHelper.addJiraChartParameter(urlBuilder, parameters, this.getChartParameters());
            return this.base64JiraChartImageService.getBase64JiraChartImageModel(parameters.get("serverId"), urlBuilder.toString());
        }
        catch (ResponseException e) {
            log.debug("Can not retrieve jira chart image for export pdf");
            throw new MacroExecutionException((Throwable)e);
        }
    }

    @Override
    public boolean isVerifyChartSupported() {
        return true;
    }
}

