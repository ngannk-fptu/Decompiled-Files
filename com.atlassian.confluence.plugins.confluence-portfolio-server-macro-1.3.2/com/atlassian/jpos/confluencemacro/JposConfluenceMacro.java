/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.jpos.confluencemacro;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JposConfluenceMacro
implements Macro {
    static final String URL_PARAM_NAME = "url";
    static final String HEIGHT_PARAM_NAME = "planHeight";
    static final int MINIMUM_HEIGHT_VALUE = 300;
    static final String DEFAULT_HEIGHT_VALUE = "700";
    static final String JPO_APPLINK_CHECK_URL = "/rest/jpo/1.0/authentication/test";
    private static final Pattern PLAN_SHARE_LINK_PATTERN = Pattern.compile("^https?://.+/secure/PortfolioRoadmapConfluence.jspa\\?r=[a-zA-Z0-9]{0,30}$");
    private static final String APPLINK_PROXY_PLAN_URL_FORMAT = "{0}/plugins/servlet/PortfolioRoadmapConfluence?appId={1}&{2}";
    private final ApplicationProperties applicationProperties;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final I18nResolver i18nResolver;
    private final VelocityHelperService velocityHelperService;

    @Autowired
    public JposConfluenceMacro(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport ReadOnlyApplicationLinkService applicationLinkService, @ComponentImport I18nResolver i18nResolver, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport PermissionManager permissionManager) {
        this.applicationProperties = applicationProperties;
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
        this.velocityHelperService = velocityHelperService;
    }

    public String execute(Map<String, String> params, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
        String height = this.validateMacroIFrameHeight(params.get(HEIGHT_PARAM_NAME));
        URI planShareLink = this.validatePlanUrl(params.get(URL_PARAM_NAME));
        ReadOnlyApplicationLink applicationLink = this.find(planShareLink);
        Map context = this.velocityHelperService.createDefaultVelocityContext();
        try {
            this.checkAppLink(applicationLink);
        }
        catch (CredentialsRequiredException e) {
            context.put("authenticationUrl", e.getAuthorisationURI());
            return this.renderVelocityTemplate("authenticate_plan.vm", context);
        }
        catch (ResponseStatusException e) {
            if (e.getResponse() != null && e.getResponse().getStatusCode() == 401) {
                String authenticateHeader = e.getResponse().getHeader("WWW-Authenticate");
                if (authenticateHeader != null && authenticateHeader.contains("oauth_problem=\"token_rejected\"")) {
                    context.put("authenticationUrl", applicationLink.createAuthenticatedRequestFactory().getAuthorisationURI());
                    return this.renderVelocityTemplate("authenticate_plan.vm", context);
                }
                throw new MacroExecutionException(this.notAuthorizedErrorMessage());
            }
            throw new MacroExecutionException((Throwable)e);
        }
        catch (Exception e) {
            throw new MacroExecutionException((Throwable)e);
        }
        String planShareAppLink = this.createProxyPlanUrl(applicationLink, planShareLink);
        context.put("planUrl", planShareAppLink);
        context.put("height", height);
        return this.renderVelocityTemplate("template.vm", context);
    }

    private String createProxyPlanUrl(ReadOnlyApplicationLink applicationLink, URI planShareLink) {
        return MessageFormat.format(APPLINK_PROXY_PLAN_URL_FORMAT, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), applicationLink.getId().get(), StringUtils.defaultString((String)planShareLink.getQuery()));
    }

    @VisibleForTesting
    void checkAppLink(ReadOnlyApplicationLink applicationLink) throws ResponseException, CredentialsRequiredException {
        applicationLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.GET, JPO_APPLINK_CHECK_URL).execute();
    }

    @VisibleForTesting
    String renderVelocityTemplate(String templateName, Map<String, Object> context) {
        return this.velocityHelperService.getRenderedTemplate(templateName, context);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    @VisibleForTesting
    URI validatePlanUrl(String planShareLink) throws MacroExecutionException {
        String trimmedPlanShareLink = StringUtils.trimToEmpty((String)planShareLink);
        Matcher planShareLinkMatcher = PLAN_SHARE_LINK_PATTERN.matcher(trimmedPlanShareLink);
        if (!planShareLinkMatcher.find()) {
            throw new MacroExecutionException(this.urlErrorMessage());
        }
        try {
            return new URI(trimmedPlanShareLink);
        }
        catch (URISyntaxException e) {
            throw new MacroExecutionException(this.urlErrorMessage(), (Throwable)e);
        }
    }

    @VisibleForTesting
    String validateMacroIFrameHeight(String height) throws MacroExecutionException {
        try {
            String trimmedHeight = (String)StringUtils.defaultIfEmpty((CharSequence)StringUtils.trim((String)height), (CharSequence)DEFAULT_HEIGHT_VALUE);
            if (Integer.parseInt(trimmedHeight) >= 300) {
                return trimmedHeight;
            }
            return String.valueOf(300);
        }
        catch (NumberFormatException e) {
            throw new MacroExecutionException(this.invalidHeightErrorMessage());
        }
    }

    private ReadOnlyApplicationLink find(URI planShareLink) throws MacroExecutionException {
        Iterable applicationLinks = this.applicationLinkService.getApplicationLinks(JiraApplicationType.class);
        return StreamSupport.stream(applicationLinks.spliterator(), false).filter(applicationLink -> planShareLink.toString().startsWith(applicationLink.getRpcUrl().toString())).findFirst().orElseThrow(() -> new MacroExecutionException(this.appLinkErrorMessage()));
    }

    private String invalidHeightErrorMessage() {
        return this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-portfolio-server-macro.portfolio-for-jira-plan.param.height.error");
    }

    private String notAuthorizedErrorMessage() {
        return this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-portfolio-server-macro.portfolio-for-jira-plan.applink.notpermitted.error");
    }

    private String urlErrorMessage() {
        return this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-portfolio-server-macro.portfolio-for-jira-plan.param.url.error");
    }

    private String appLinkErrorMessage() {
        return this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-portfolio-server-macro.portfolio-for-jira-plan.param.applink.error");
    }
}

