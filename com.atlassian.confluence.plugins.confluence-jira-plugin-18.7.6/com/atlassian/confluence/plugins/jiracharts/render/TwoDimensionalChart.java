/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody
 *  com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.plugins.jiracharts.model.TwoDimensionalChartModel;
import com.atlassian.confluence.plugins.jiracharts.render.JiraHtmlChart;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class TwoDimensionalChart
extends JiraHtmlChart {
    private static final String[] chartParameters = new String[]{"xstattype", "ystattype"};
    private static final String MAX_NUMBER_TO_SHOW_VALUE = "9999";
    private static final String IS_SHOW_MORE_PARAM = "isShowMore";
    private static final String STATUSES_PARAM_VALUE = "statuses";
    private static final String ISSUE_TYPE_PARAM_VALUE = "issuetype";
    private static final String DEFAULT_PLACEHOLDER_IMG_PATH = "/download/resources/confluence.extra.jira/jirachart_images/twodimensional-chart-placeholder.png";
    private static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img src=\"(.*?)\"");
    private static final Pattern LINK_PATTERN = Pattern.compile("<a href=[\"'](.*?)[\"']");
    private static final Random RANDOM = new Random();
    private final ContextPathHolder contextPathHolder;
    private final VelocityHelperService velocityHelperService;
    private MacroMarshallingFactory macroMarshallingFactory;

    public TwoDimensionalChart(ReadOnlyApplicationLinkService applicationLinkService, MacroMarshallingFactory macroMarshallingFactory, I18nResolver i18nResolver, ContextPathHolder contextPathHolder, VelocityHelperService velocityHelperService) {
        super(applicationLinkService, i18nResolver);
        this.macroMarshallingFactory = macroMarshallingFactory;
        this.contextPathHolder = contextPathHolder;
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public Map<String, Object> setupContext(Map<String, String> parameters, JQLValidationResult result, ConversionContext context) {
        String numberToShow = this.getNumberToShow(context, parameters.get("numberToShow"));
        String jql = GeneralUtil.urlDecode((String)parameters.get("jql"));
        Map<String, Object> contextMap = JiraChartHelper.getCommonChartContext(parameters, result, context, this.velocityHelperService);
        try {
            TwoDimensionalChartModel chart = (TwoDimensionalChartModel)this.getChartModel(parameters.get("serverId"), this.buildTwoDimensionalRestURL(parameters, numberToShow, jql));
            this.updateStatusIconLink(parameters, chart, result.getDisplayUrl());
            this.updateIssueTypeIconLink(parameters, chart, result.getRpcUrl(), result.getDisplayUrl());
            this.rebaseLinks(chart, result.getRpcUrl(), result.getDisplayUrl());
            contextMap.put("chartModel", chart);
            contextMap.put("numberRowShow", this.getNumberRowShow(numberToShow, chart.getTotalRows()));
            contextMap.put("jql", jql);
            if (this.isShowLink(context, numberToShow, chart.getTotalRows())) {
                this.setupShowLink(contextMap, parameters, context);
            }
        }
        catch (Exception e) {
            contextMap.put("error", e.getMessage());
        }
        return contextMap;
    }

    public Class<TwoDimensionalChartModel> getChartModelClass() {
        return TwoDimensionalChartModel.class;
    }

    @Override
    public String getImagePlaceholderUrl(Map<String, String> parameters, UrlBuilder urlBuilder) {
        return this.contextPathHolder.getContextPath() + DEFAULT_PLACEHOLDER_IMG_PATH;
    }

    @Override
    public String getDefaultImagePlaceholderUrl() {
        return this.contextPathHolder.getContextPath() + DEFAULT_PLACEHOLDER_IMG_PATH;
    }

    @Override
    public String getJiraGadgetRestUrl() {
        return "/rest/gadget/1.0/twodimensionalfilterstats/generate?filterId=jql-";
    }

    @Override
    public String getTemplateFileName() {
        return "two-dimensional-chart.vm";
    }

    @Override
    public String[] getChartParameters() {
        return chartParameters;
    }

    private int getNextRefreshId() {
        return RANDOM.nextInt();
    }

    private String getNumberToShow(ConversionContext context, String numberToShow) {
        if (context.hasProperty(IS_SHOW_MORE_PARAM) && Boolean.valueOf(context.getPropertyAsString(IS_SHOW_MORE_PARAM)).booleanValue()) {
            return MAX_NUMBER_TO_SHOW_VALUE;
        }
        if (StringUtils.isBlank((CharSequence)numberToShow)) {
            return MAX_NUMBER_TO_SHOW_VALUE;
        }
        return numberToShow;
    }

    private boolean isShowLink(ConversionContext context, String numberToShow, int totalRow) {
        return context.hasProperty(IS_SHOW_MORE_PARAM) || Integer.parseInt(numberToShow) < totalRow;
    }

    private void setupShowLink(Map<String, Object> contextMap, Map<String, String> parameters, ConversionContext context) throws MacroExecutionException {
        contextMap.put("showLink", true);
        String isShowMore = context.getPropertyAsString(IS_SHOW_MORE_PARAM);
        contextMap.put(IS_SHOW_MORE_PARAM, !Boolean.parseBoolean(isShowMore));
        contextMap.put("chartId", this.getNextRefreshId());
        MacroDefinition macroDefinition = MacroDefinition.builder((String)"jirachart").withMacroBody((MacroBody)new RichTextMacroBody("")).withParameters(parameters).build();
        try {
            Streamable out = this.macroMarshallingFactory.getStorageMarshaller().marshal((Object)macroDefinition, context);
            StringWriter writer = new StringWriter();
            out.writeTo((Writer)writer);
            contextMap.put("wikiMarkup", writer.toString());
        }
        catch (XhtmlException | IOException e) {
            throw new MacroExecutionException("Unable to construct macro definition.", e);
        }
        String contentId = context.getEntity() != null ? context.getEntity().getIdAsString() : "-1";
        contextMap.put("contentId", contentId);
    }

    private String buildTwoDimensionalRestURL(Map<String, String> parameters, String numberToShow, String jql) {
        UrlBuilder urlBuilder = new UrlBuilder(this.getJiraGadgetRestUrl() + GeneralUtil.urlEncode((String)jql, (String)"UTF-8"));
        JiraChartHelper.addJiraChartParameter(urlBuilder, parameters, this.getChartParameters());
        urlBuilder.add("sortBy", "natural");
        urlBuilder.add("showTotals", true);
        urlBuilder.add("numberToShow", numberToShow);
        urlBuilder.add("sortDirection", "asc");
        return urlBuilder.toString();
    }

    private String getNumberRowShow(String numberToShow, int totalRows) {
        if (StringUtils.isNumeric((CharSequence)numberToShow) && Integer.parseInt(numberToShow) > totalRows) {
            return String.valueOf(totalRows);
        }
        return numberToShow;
    }

    private void updateStatusIconLink(Map<String, String> parameters, TwoDimensionalChartModel chart, String displayURL) throws URISyntaxException {
        if (STATUSES_PARAM_VALUE.equals(parameters.get("xstattype")) || STATUSES_PARAM_VALUE.equals(parameters.get("ystattype"))) {
            String uri = TwoDimensionalChart.getDisplayURI(displayURL);
            if (STATUSES_PARAM_VALUE.equals(parameters.get("xstattype"))) {
                List<TwoDimensionalChartModel.Cell> cells = chart.getFirstRow().getCells();
                for (TwoDimensionalChartModel.Cell cell : cells) {
                    cell.setMarkup(TwoDimensionalChart.getStatusMarkup(cell.getMarkup(), uri));
                }
            }
            if (STATUSES_PARAM_VALUE.equals(parameters.get("ystattype"))) {
                TwoDimensionalChartModel.Row row;
                List<TwoDimensionalChartModel.Row> rows = chart.getRows();
                Iterator<Object> iterator = rows.iterator();
                while (iterator.hasNext() && !(row = (TwoDimensionalChartModel.Row)iterator.next()).getCells().isEmpty()) {
                    TwoDimensionalChartModel.Cell firstCell = row.getCells().get(0);
                    firstCell.setMarkup(TwoDimensionalChart.getStatusMarkup(firstCell.getMarkup(), uri));
                }
            }
        }
    }

    private static String getStatusMarkup(String markup, String displayUrl) {
        String imgUrl;
        Matcher matcher = IMG_SRC_PATTERN.matcher(markup);
        if (matcher.find() && matcher.groupCount() > 0 && !(imgUrl = matcher.group(1)).matches("^(http://|https://).*")) {
            return markup.replace(imgUrl, displayUrl + imgUrl);
        }
        return markup;
    }

    private void updateIssueTypeIconLink(Map<String, String> parameters, TwoDimensionalChartModel chart, String rpcUrl, String displayUrl) {
        if (ISSUE_TYPE_PARAM_VALUE.equals(parameters.get("xstattype")) || ISSUE_TYPE_PARAM_VALUE.equals(parameters.get("ystattype"))) {
            if (ISSUE_TYPE_PARAM_VALUE.equals(parameters.get("xstattype"))) {
                List<TwoDimensionalChartModel.Cell> cells = chart.getFirstRow().getCells();
                for (TwoDimensionalChartModel.Cell cell : cells) {
                    cell.setMarkup(TwoDimensionalChart.getIssueTypeMarkup(cell.getMarkup(), rpcUrl, displayUrl));
                }
            }
            if (ISSUE_TYPE_PARAM_VALUE.equals(parameters.get("ystattype"))) {
                TwoDimensionalChartModel.Row row;
                List<TwoDimensionalChartModel.Row> rows = chart.getRows();
                Iterator<Object> iterator = rows.iterator();
                while (iterator.hasNext() && !(row = (TwoDimensionalChartModel.Row)iterator.next()).getCells().isEmpty()) {
                    TwoDimensionalChartModel.Cell firstCell = row.getCells().get(0);
                    firstCell.setMarkup(TwoDimensionalChart.getIssueTypeMarkup(firstCell.getMarkup(), rpcUrl, displayUrl));
                }
            }
        }
    }

    private static String getIssueTypeMarkup(String markup, String rpcUrl, String displayUrl) {
        Matcher matcher = IMG_SRC_PATTERN.matcher(markup);
        if (matcher.find() && matcher.groupCount() > 0) {
            String imgUrl = matcher.group(1);
            String displayImgUrl = imgUrl.replace(rpcUrl, displayUrl);
            return markup.replace(imgUrl, displayImgUrl);
        }
        return markup;
    }

    private void rebaseLinks(TwoDimensionalChartModel chart, String rpcURL, String displayURL) {
        List<TwoDimensionalChartModel.Row> rows = chart.getRows();
        if (rows != null && !rows.isEmpty()) {
            for (TwoDimensionalChartModel.Row row : rows) {
                List<TwoDimensionalChartModel.Cell> cells = row.getCells();
                for (TwoDimensionalChartModel.Cell cell : cells) {
                    cell.setMarkup(TwoDimensionalChart.getCellMarkup(cell.getMarkup(), rpcURL, displayURL));
                }
            }
        }
    }

    private static String getCellMarkup(String markup, String rpcUrl, String displayUrl) {
        Matcher matcher = LINK_PATTERN.matcher(markup);
        if (matcher.find() && matcher.groupCount() > 0) {
            String imgUrl = matcher.group(1);
            String displayImgUrl = imgUrl.replace(rpcUrl, displayUrl);
            return markup.replace(imgUrl, displayImgUrl);
        }
        return markup;
    }

    private static String getDisplayURI(String displayURL) throws URISyntaxException {
        URI uri = new URI(displayURL);
        return uri.getScheme() + "://" + uri.getAuthority();
    }
}

