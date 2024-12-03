/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.StreamableMacro
 *  org.jdom.Element
 */
package com.atlassian.confluence.extra.jira.executor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.exception.UnsupportedJiraServerException;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.macro.StreamableMacro;
import java.util.Map;
import java.util.concurrent.Callable;
import org.jdom.Element;

public class StreamableMacroFutureTask
implements Callable<String> {
    private final Map<String, String> parameters;
    private final ConversionContext context;
    private final StreamableMacro macro;
    private final Element element;
    private final String jiraDisplayUrl;
    private final String jiraRpcUrl;
    private final Exception exception;
    private final JiraExceptionHelper jiraExceptionHelper;

    public StreamableMacroFutureTask(JiraExceptionHelper jiraExceptionHelper, Map<String, String> parameters, ConversionContext context, StreamableMacro macro) {
        this(jiraExceptionHelper, parameters, context, macro, null, null, null, null);
    }

    public StreamableMacroFutureTask(JiraExceptionHelper jiraExceptionHelper, Map<String, String> parameters, ConversionContext context, StreamableMacro macro, Element element, String jiraDisplayUrl, String jiraRpcUrl, Exception exception) {
        this.parameters = parameters;
        this.context = context;
        this.macro = macro;
        this.element = element;
        this.jiraDisplayUrl = jiraDisplayUrl;
        this.jiraRpcUrl = jiraRpcUrl;
        this.exception = exception;
        this.jiraExceptionHelper = jiraExceptionHelper;
    }

    @Override
    public String call() {
        return this.renderValue();
    }

    public String renderValue() {
        long remainingTimeout = this.context.getTimeout().getTime();
        if (remainingTimeout <= 0L) {
            return this.jiraExceptionHelper.renderTimeoutMessage(this.parameters);
        }
        try {
            if (this.element != null) {
                JiraIssuesMacro jiraIssuesMacro = (JiraIssuesMacro)this.macro;
                return jiraIssuesMacro.renderSingleJiraIssue(this.parameters, this.context, this.element, this.jiraDisplayUrl, this.jiraRpcUrl);
            }
            if (this.exception != null) {
                if (this.exception instanceof UnsupportedJiraServerException) {
                    return this.macro.execute(this.parameters, null, this.context);
                }
                return this.jiraExceptionHelper.renderBatchingJIMExceptionMessage(this.exception.getMessage(), this.parameters);
            }
            return this.macro.execute(this.parameters, null, this.context);
        }
        catch (Exception e) {
            return this.jiraExceptionHelper.renderNormalJIMExceptionMessage(e);
        }
    }
}

