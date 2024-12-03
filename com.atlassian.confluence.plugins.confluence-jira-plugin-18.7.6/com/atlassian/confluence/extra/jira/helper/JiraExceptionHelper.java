/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.extra.jira.ApplicationLinkResolver;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.exception.AuthenticationException;
import com.atlassian.confluence.extra.jira.exception.JiraIssueDataException;
import com.atlassian.confluence.extra.jira.exception.JiraIssueMacroException;
import com.atlassian.confluence.extra.jira.exception.JiraPermissionException;
import com.atlassian.confluence.extra.jira.exception.JiraRuntimeException;
import com.atlassian.confluence.extra.jira.exception.MalformedRequestException;
import com.atlassian.confluence.extra.jira.exception.TrustedAppsException;
import com.atlassian.confluence.extra.jira.util.JiraIssueUtil;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraExceptionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraExceptionHelper.class);
    private static final String MACRO_NAME = "macroName";
    private final I18nResolver i18nResolver;
    private final ApplicationLinkResolver applicationLinkResolver;
    private final VelocityHelperService velocityHelperService;
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String TEMPLATE_PATH = "templates/extra/jira";
    private static final String JIRA_LINK_TEXT = "jiraLinkText";

    public JiraExceptionHelper(I18nResolver i18nResolver, ApplicationLinkResolver applicationLinkResolver, VelocityHelperService velocityHelperService) {
        this.i18nResolver = i18nResolver;
        this.applicationLinkResolver = applicationLinkResolver;
        this.velocityHelperService = velocityHelperService;
    }

    public void throwMacroExecutionException(Exception exception, ConversionContext conversionContext) throws MacroExecutionException {
        String i18nKey;
        List<String> params = null;
        if (exception instanceof UnknownHostException) {
            i18nKey = "jiraissues.error.unknownhost";
            params = Collections.singletonList(StringUtils.defaultString((String)exception.getMessage()));
        } else if (exception instanceof ConnectException || exception instanceof SocketException) {
            i18nKey = "jiraissues.error.unabletoconnect";
            params = Collections.singletonList(StringUtils.defaultString((String)exception.getMessage()));
        } else if (exception instanceof AuthenticationException) {
            i18nKey = "jiraissues.error.authenticationerror";
        } else if (exception instanceof MalformedRequestException || exception instanceof JiraPermissionException) {
            i18nKey = "jiraissues.error.notpermitted";
        } else if (exception instanceof TrustedAppsException) {
            i18nKey = "jiraissues.error.trustedapps";
            params = Collections.singletonList(exception.getMessage());
        } else if (exception instanceof TypeNotInstalledException) {
            i18nKey = "jirachart.error.applicationLinkNotExist";
            params = Collections.singletonList(exception.getMessage());
        } else if (exception instanceof JiraRuntimeException) {
            i18nKey = "jiraissues.error.request.handling";
            params = Collections.singletonList(exception.getMessage());
        } else if (exception instanceof JiraIssueDataException) {
            i18nKey = "jiraissues.error.nodata";
        } else if (exception instanceof SocketTimeoutException) {
            i18nKey = "jiraissues.error.timeout.connection";
        } else if (exception instanceof NotAuthorizedException) {
            i18nKey = "jiraissues.error.notwhitelisted";
        } else {
            i18nKey = "jiraissues.unexpected.error";
            if (!ConversionContextOutputType.FEED.value().equals(conversionContext.getOutputType())) {
                LOGGER.error("Macro execution exception: ", (Throwable)exception);
            }
        }
        String msg = params != null ? this.getText(i18nKey, params.toArray(new Object[0])) : this.getText(i18nKey, new Object[0]);
        throw new MacroExecutionException(msg, (Throwable)exception);
    }

    public String getText(String i18n, Object ... substitutions) {
        return this.i18nResolver.getText(i18n, new Serializable[]{substitutions});
    }

    public String renderExceptionMessage(String exceptionMessage) {
        return this.renderJiraIssueException(new JiraExceptionBean(exceptionMessage));
    }

    public String renderBatchingJIMExceptionMessage(String exceptionMessage, Map<String, String> parameters) {
        JiraExceptionBean exceptionBean = new JiraExceptionBean(exceptionMessage);
        exceptionBean.setIssueType(JiraIssuesMacro.JiraIssuesType.SINGLE);
        String key = JiraUtil.getSingleIssueKey(parameters);
        if (StringUtils.isNotBlank((CharSequence)key)) {
            exceptionBean.setClickableUrl(this.getJiraUrlOfBatchingIssues(parameters, key));
            exceptionBean.setJiraLinkText(key);
        }
        return this.renderJiraIssueException(exceptionBean);
    }

    public String renderNormalJIMExceptionMessage(Exception e) {
        JiraExceptionBean exceptionBean = new JiraExceptionBean(e.getMessage());
        if (e instanceof JiraIssueMacroException && ((JiraIssueMacroException)((Object)e)).getContextMap() != null) {
            exceptionBean.setMessage(e.getCause().getMessage());
            this.setupErrorJiraLink(exceptionBean, ((JiraIssueMacroException)((Object)e)).getContextMap());
        }
        return this.renderJiraIssueException(exceptionBean);
    }

    private String renderJiraIssueException(JiraExceptionBean exceptionBean) {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put(MACRO_NAME, "Jira Issues Macro");
        contextMap.put(EXCEPTION_MESSAGE, exceptionBean.getMessage());
        contextMap.put("issueType", exceptionBean.getIssueType());
        contextMap.put("columns", exceptionBean.getColumns());
        if (StringUtils.isNotBlank((CharSequence)exceptionBean.getClickableUrl())) {
            contextMap.put("clickableUrl", exceptionBean.getClickableUrl());
            contextMap.put(JIRA_LINK_TEXT, exceptionBean.getJiraLinkText());
        }
        return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/exception.vm", contextMap);
    }

    private void setupErrorJiraLink(JiraExceptionBean exceptionBean, Map<String, Object> jiraIssueMap) {
        Object issueColumnsObject;
        Object issueTypeObject;
        Object clickableURL = jiraIssueMap.get("clickableUrl");
        if (clickableURL != null) {
            exceptionBean.setClickableUrl(clickableURL.toString());
        }
        if ((issueTypeObject = jiraIssueMap.get("issueType")) != null) {
            JiraIssuesMacro.JiraIssuesType issuesType = (JiraIssuesMacro.JiraIssuesType)((Object)issueTypeObject);
            exceptionBean.setIssueType(issuesType);
            switch (issuesType) {
                case SINGLE: {
                    exceptionBean.setJiraLinkText(jiraIssueMap.get("key").toString());
                    break;
                }
                default: {
                    exceptionBean.setJiraLinkText(this.getText("view.these.issues.jira", new Object[0]));
                }
            }
        }
        if ((issueColumnsObject = jiraIssueMap.get("columns")) != null) {
            Set issueColumns = (Set)issueColumnsObject;
            exceptionBean.setColumns(issueColumns);
        }
    }

    public String renderTimeoutMessage(Map<String, String> parameters) {
        return this.renderBatchingJIMExceptionMessage(this.i18nResolver.getText("jiraissues.error.timeout.execution"), parameters);
    }

    private String getJiraUrlOfBatchingIssues(Map<String, String> parameters, String key) {
        try {
            ReadOnlyApplicationLink appLink = this.applicationLinkResolver.resolve(JiraIssuesMacro.Type.KEY, key, parameters);
            return appLink == null ? null : JiraIssueUtil.getClickableUrl(key, JiraIssuesMacro.Type.KEY, appLink, null);
        }
        catch (TypeNotInstalledException e) {
            return null;
        }
    }

    static class JiraExceptionBean {
        private String message;
        private String jiraLinkText;
        private String clickableUrl;
        private Set<String> columns;
        private JiraIssuesMacro.JiraIssuesType issueType = JiraIssuesMacro.JiraIssuesType.SINGLE;

        private JiraExceptionBean(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private String getJiraLinkText() {
            return this.jiraLinkText;
        }

        private void setJiraLinkText(String jiraLinkText) {
            this.jiraLinkText = jiraLinkText;
        }

        public String getClickableUrl() {
            return this.clickableUrl;
        }

        public void setClickableUrl(String clickableUrl) {
            this.clickableUrl = clickableUrl;
        }

        private JiraIssuesMacro.JiraIssuesType getIssueType() {
            return this.issueType;
        }

        private void setIssueType(JiraIssuesMacro.JiraIssuesType issueType) {
            this.issueType = issueType;
        }

        public Set<String> getColumns() {
            return this.columns;
        }

        public void setColumns(Set<String> columns) {
            this.columns = columns;
        }
    }
}

