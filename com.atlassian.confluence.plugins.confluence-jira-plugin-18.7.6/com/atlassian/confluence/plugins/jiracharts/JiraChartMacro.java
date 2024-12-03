/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jiracharts;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.extra.jira.executor.FutureStreamableConverter;
import com.atlassian.confluence.extra.jira.executor.MacroExecutorService;
import com.atlassian.confluence.extra.jira.executor.StreamableMacroFutureTask;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.jiracharts.DefaultJQLValidator;
import com.atlassian.confluence.plugins.jiracharts.JQLValidator;
import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChart;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChartFactory;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Map;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraChartMacro
implements StreamableMacro,
CustomHtmlEditorPlaceholder {
    private static final String TEMPLATE_PATH = "templates/jirachart/";
    private static Logger log = LoggerFactory.getLogger(JiraChartMacro.class);
    private final MacroExecutorService executorService;
    private final I18nResolver i18nResolver;
    private final JiraConnectorManager jiraConnectorManager;
    private final JiraChartFactory jiraChartFactory;
    private final JiraExceptionHelper jiraExceptionHelper;
    private final VelocityHelperService velocityHelperService;
    private ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private JQLValidator jqlValidator;

    public JiraChartMacro(MacroExecutorService executorService, ReadOnlyApplicationLinkService readOnlyApplicationLinkService, I18nResolver i18nResolver, JiraConnectorManager jiraConnectorManager, JiraChartFactory jiraChartFactory, JiraExceptionHelper jiraExceptionHelper, VelocityHelperService velocityHelperService) {
        this.executorService = executorService;
        this.i18nResolver = i18nResolver;
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.jiraConnectorManager = jiraConnectorManager;
        this.jiraChartFactory = jiraChartFactory;
        this.jiraExceptionHelper = jiraExceptionHelper;
        this.velocityHelperService = velocityHelperService;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        String chartType = parameters.get("chartType");
        if (!JiraChartHelper.isSupportedChart(chartType)) {
            throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.not.supported"));
        }
        JiraChart jiraChart = this.jiraChartFactory.getJiraChartRenderer(chartType);
        JQLValidationResult result = this.getJqlValidator().doValidate(parameters, jiraChart.isVerifyChartSupported());
        Map<String, Object> contextMap = jiraChart.setupContext(parameters, result, context);
        return this.velocityHelperService.getRenderedTemplate(TEMPLATE_PATH + jiraChart.getTemplateFileName(), contextMap);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String getCustomPlaceholder(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        try {
            String chartType = parameters.get("chartType");
            JiraChart jiraChart = this.jiraChartFactory.getJiraChartRenderer(chartType);
            if (chartType.equals("twodimensional")) {
                return "<img src=\"" + jiraChart.getDefaultImagePlaceholderUrl() + "\">";
            }
            JQLValidationResult result = this.getJqlValidator().doValidate(parameters, jiraChart.isVerifyChartSupported());
            Map<String, Object> contextMap = jiraChart.setupContext(parameters, result, conversionContext);
            return "<img src=\"" + contextMap.get("srcImg") + "\">";
        }
        catch (Exception e) {
            log.error("Error getting Jira Chart Macro image placeholder", (Throwable)e);
            return null;
        }
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext context) {
        Future<String> futureResult = this.executorService.submit(new StreamableMacroFutureTask(this.jiraExceptionHelper, parameters, context, this));
        return new FutureStreamableConverter.Builder(futureResult, context, this.i18nResolver, this.jiraExceptionHelper).executionErrorMsg("jirachart.error.execution").executionTimeoutErrorMsg("jirachart.error.timeout.execution").connectionTimeoutErrorMsg("jirachart.error.timeout.connection").interruptedErrorMsg("jirachart.error.interrupted").build();
    }

    public JQLValidator getJqlValidator() {
        if (this.jqlValidator == null) {
            this.setJqlValidator(new DefaultJQLValidator(this.readOnlyApplicationLinkService, this.i18nResolver, this.jiraConnectorManager));
        }
        return this.jqlValidator;
    }

    public void setJqlValidator(JQLValidator jqlValidator) {
        this.jqlValidator = jqlValidator;
    }
}

