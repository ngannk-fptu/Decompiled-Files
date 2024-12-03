/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.web.UrlBuilder
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.jiracharts.Base64JiraChartImageService;
import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.plugins.jiracharts.model.JiraImageChartModel;
import com.atlassian.confluence.plugins.jiracharts.render.JiraImageChart;
import com.atlassian.confluence.web.UrlBuilder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class PieChart
extends JiraImageChart {
    private static final String PARAM_STAT_TYPE = "statType";
    private static final String[] chartParameters = new String[]{"statType"};
    private static final String DEFAULT_PLACEHOLDER_IMG_PATH = "/download/resources/confluence.extra.jira/jirachart_images/jirachart_placeholder.png";
    private final VelocityHelperService velocityHelperService;

    public PieChart(ContextPathHolder pathHolder, Base64JiraChartImageService base64JiraChartImageService, VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
        this.base64JiraChartImageService = base64JiraChartImageService;
        this.pathHolder = pathHolder;
    }

    @Override
    public String[] getChartParameters() {
        return chartParameters;
    }

    @Override
    public String getTemplateFileName() {
        return "piechart.vm";
    }

    @Override
    public String getJiraGadgetRestUrl() {
        return "/rest/gadget/1.0/piechart/generate?projectOrFilterId=jql-";
    }

    @Override
    public String getJiraGadgetUrl(HttpServletRequest request) {
        UrlBuilder urlBuilder = JiraChartHelper.getCommonJiraGadgetUrl(request.getParameter("jql"), request.getParameter("width"), this.getJiraGadgetRestUrl());
        JiraChartHelper.addJiraChartParameter(urlBuilder, request, this.getChartParameters());
        return urlBuilder.toString();
    }

    @Override
    public Map<String, Object> setupContext(Map<String, String> parameters, JQLValidationResult result, ConversionContext context) throws MacroExecutionException {
        Map<String, Object> contextMap = JiraChartHelper.getCommonChartContext(parameters, result, context, this.velocityHelperService);
        JiraImageChartModel chartModel = this.getImageSourceModel(parameters, context.getOutputType());
        contextMap.put("srcImg", chartModel.getBase64Image());
        contextMap.put(PARAM_STAT_TYPE, chartModel.getStatType());
        return contextMap;
    }

    @Override
    public String getImagePlaceholderUrl(Map<String, String> parameters, UrlBuilder urlBuilder) {
        return DEFAULT_PLACEHOLDER_IMG_PATH;
    }

    @Override
    public String getDefaultImagePlaceholderUrl() {
        return DEFAULT_PLACEHOLDER_IMG_PATH;
    }

    @Override
    public String getDefaultPDFChartWidth() {
        return "320";
    }
}

