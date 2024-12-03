/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.web.UrlBuilder
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.jiracharts.helper;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.UrlBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class JiraChartHelper {
    public static final String PARAM_JQL = "jql";
    public static final String PARAM_CHART_TYPE = "chartType";
    public static final String PARAM_SERVER_ID = "serverId";
    public static final String PARAM_WIDTH = "width";
    public static final String PARAM_AUTHENTICATED = "authenticated";
    private static final String PDF_EXPORT = "pdfExport";
    private static final String EMAIL = "email";
    private static final String PARAM_HEIGHT = "height";
    private static final String SERVLET_JIRA_CHART_URI = "/plugins/servlet/jira-chart-proxy";
    private static final List<String> supportedCharts = Collections.unmodifiableList(Arrays.asList("pie", "createdvsresolved", "twodimensional"));

    public static Map<String, Object> getCommonChartContext(Map<String, String> parameters, JQLValidationResult result, ConversionContext context, VelocityHelperService velocityHelperService) {
        Map contextMap = velocityHelperService.createDefaultVelocityContext();
        Boolean isShowBorder = Boolean.parseBoolean(parameters.get("border"));
        Boolean isShowInfor = Boolean.parseBoolean(parameters.get("showinfor"));
        boolean isPreviewMode = ConversionContextOutputType.PREVIEW.name().equalsIgnoreCase(context.getOutputType());
        contextMap.put("jqlValidationResult", result);
        contextMap.put("showBorder", isShowBorder);
        contextMap.put("showInfor", isShowInfor);
        contextMap.put("isPreviewMode", isPreviewMode);
        if ("pdf".equals(context.getOutputType())) {
            contextMap.put(PDF_EXPORT, Boolean.TRUE);
        } else if (EMAIL.equals(context.getOutputType())) {
            contextMap.put(EMAIL, Boolean.TRUE);
        }
        return contextMap;
    }

    public static UrlBuilder getCommonJiraGadgetUrl(String jql, String width, String gadgetUrl) {
        String jqlDecodeValue = GeneralUtil.urlDecode((String)jql);
        UrlBuilder urlBuilder = new UrlBuilder(gadgetUrl + GeneralUtil.urlEncode((String)jqlDecodeValue, (String)"UTF-8"));
        JiraChartHelper.addSizeParam(urlBuilder, width);
        return urlBuilder;
    }

    public static void addJiraChartParameter(UrlBuilder urlBuilders, Map<String, String> map, String[] parameters) {
        for (String parameter : parameters) {
            if (map.get(parameter) == null) continue;
            urlBuilders.add(parameter, map.get(parameter));
        }
    }

    public static void addJiraChartParameter(UrlBuilder urlBuilders, HttpServletRequest request, String[] parameters) {
        for (String parameter : parameters) {
            if (request.getParameter(parameter) == null) continue;
            urlBuilders.add(parameter, request.getParameterValues(parameter));
        }
    }

    public static boolean isRequiredParamValid(HttpServletRequest request) {
        return StringUtils.isNotBlank((CharSequence)request.getParameter(PARAM_CHART_TYPE));
    }

    public static boolean isSupportedChart(String chartType) {
        return StringUtils.isNotBlank((CharSequence)chartType) && supportedCharts.contains(chartType);
    }

    private static UrlBuilder addSizeParam(UrlBuilder urlBuilder, String width) {
        if (StringUtils.isNotBlank((CharSequence)width)) {
            String height = String.valueOf(Integer.parseInt(width) * 2 / 3);
            urlBuilder.add(PARAM_WIDTH, width).add(PARAM_HEIGHT, height);
        }
        return urlBuilder;
    }
}

