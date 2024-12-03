/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody
 *  com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.ResourceAware
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.Gson
 *  com.google.gson.JsonParser
 *  com.google.gson.reflect.TypeToken
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.utils.URLEncodedUtils
 *  org.apache.http.message.BasicNameValuePair
 *  org.jdom.Attribute
 *  org.jdom.DataConversionException
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.jira.ApplicationLinkResolver;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.SeraphUtils;
import com.atlassian.confluence.extra.jira.api.services.AsyncJiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraCacheManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueSortingManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.TrustedApplicationConfig;
import com.atlassian.confluence.extra.jira.columns.DefaultJiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.extra.jira.columns.JiraIssuesXmlTransformer;
import com.atlassian.confluence.extra.jira.columns.Team;
import com.atlassian.confluence.extra.jira.exception.JiraIssueDataException;
import com.atlassian.confluence.extra.jira.exception.JiraIssueMacroException;
import com.atlassian.confluence.extra.jira.exception.MalformedRequestException;
import com.atlassian.confluence.extra.jira.helper.DefaultEpicInfoRetriever;
import com.atlassian.confluence.extra.jira.helper.EpicInformationHelper;
import com.atlassian.confluence.extra.jira.helper.ImagePlaceHolderHelper;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.atlassian.confluence.extra.jira.helper.RestHelper;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.request.JiraRequestData;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.confluence.extra.jira.util.JiraIssuePdfExportUtil;
import com.atlassian.confluence.extra.jira.util.JiraIssueUtil;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.ResourceAware;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraIssuesMacro
extends BaseMacro
implements Macro,
EditorImagePlaceholder,
ResourceAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssuesMacro.class);
    private static final JsonParser parser = new JsonParser();
    private static final Random RANDOM = new Random();
    public static final String PARAM_PLACEHOLDER = "placeholder";
    public static final Set<String> DEFAULT_COLUMNS_FOR_SINGLE_ISSUE = Arrays.asList("summary", "type", "resolution", "status").stream().collect(Collectors.toCollection(LinkedHashSet::new));
    public static final String KEY = "key";
    public static final String JIRA = "jira";
    public static final String JIRAISSUES = "jiraissues";
    public static final String SHOW_SUMMARY = "showSummary";
    public static final String ITEM = "item";
    public static final String SERVER_ID = "serverId";
    public static final String CLIENT_ID = "clientId";
    public static final String CLICKABLE_URL = "clickableUrl";
    public static final String JIRA_SERVER_URL = "jiraServerUrl";
    public static final String JIRA_BROWSE_URL = "/browse/";
    public static final String TEMPLATE_PATH = "templates/extra/jira";
    private static final String MOBILE = "mobile";
    private static final String SERVER = "server";
    public static final String ISSUE_TYPE = "issueType";
    public static final String COLUMNS = "columns";
    public static final String COLUMN_IDS = "columnIds";
    private static final String TOKEN_TYPE_PARAM = ": = | TOKEN_TYPE | = :";
    private static final String RENDER_MODE_PARAM = "renderMode";
    private static final String DYNAMIC_RENDER_MODE = "dynamic";
    private static final String DEFAULT_DATA_WIDTH = "100%";
    private static final String CACHE = "cache";
    private static final String ENABLE_REFRESH = "enableRefresh";
    private static final String TOTAL_ISSUES = "totalIssues";
    private static final String TITLE = "title";
    private static final String ANONYMOUS = "anonymous";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    @VisibleForTesting
    static final String IS_NO_PERMISSION_TO_VIEW = "isNoPermissionToView";
    private static final String COUNT = "count";
    private static final String ICON_URL = "iconUrl";
    private static final String IS_ADMINISTRATOR = "isAdministrator";
    private static final String IS_SOURCE_APP_LINK = "isSourceApplink";
    private static final String MAX_ISSUES_TO_DISPLAY = "maxIssuesToDisplay";
    private static final String BASE_URL = "baseurl";
    private static final String MAXIMUM_ISSUES = "maximumIssues";
    private static final String TEMPLATE_MOBILE_PATH = "templates/mobile/extra/jira";
    private static final String DEFAULT_JIRA_ISSUES_COUNT = "0";
    private static final String EMAIL_RENDER = "email";
    private static final String PDF_EXPORT = "pdfExport";
    private static final String JIRA_PORTFOLIO_TEAM_API_URL = "/rest/teams-api/1.0/team";
    public static final List<String> MACRO_PARAMS = Arrays.asList("count", "columns", "title", "renderMode", "cache", "width", "height", "server", "serverId", "anonymous", "baseurl", "showSummary", ": = | RAW | = :", "maximumIssues", ": = | TOKEN_TYPE | = :");
    private final JiraIssuesXmlTransformer xmlXformer;
    private I18nResolver i18nResolver;
    private JiraIssuesManager jiraIssuesManager;
    private SettingsManager settingsManager;
    private JiraIssuesColumnManager jiraIssuesColumnManager;
    private TrustedApplicationConfig trustedApplicationConfig;
    private String resourcePath;
    private PermissionManager permissionManager;
    protected ApplicationLinkResolver applicationLinkResolver;
    private LocaleManager localeManager;
    private MacroMarshallingFactory macroMarshallingFactory;
    private JiraCacheManager jiraCacheManager;
    private ImagePlaceHolderHelper imagePlaceHolderHelper;
    private FormatSettingsManager formatSettingsManager;
    private JiraIssueSortingManager jiraIssueSortingManager;
    private final AsyncJiraIssueBatchService asyncJiraIssueBatchService;
    private final DarkFeatureManager darkFeatureManager;
    private final VelocityHelperService velocityHelperService;
    protected final JiraExceptionHelper jiraExceptionHelper;
    private final UserAccessor userAccessor;

    public JiraIssuesMacro(I18nResolver i18nResolver, JiraIssuesManager jiraIssuesManager, SettingsManager settingsManager, JiraIssuesColumnManager jiraIssuesColumnManager, TrustedApplicationConfig trustedApplicationConfig, PermissionManager permissionManager, ApplicationLinkResolver applicationLinkResolver, MacroMarshallingFactory macroMarshallingFactory, JiraCacheManager jiraCacheManager, ImagePlaceHolderHelper imagePlaceHolderHelper, FormatSettingsManager formatSettingsManager, JiraIssueSortingManager jiraIssueSortingManager, JiraExceptionHelper jiraExceptionHelper, LocaleManager localeManager, AsyncJiraIssueBatchService asyncJiraIssueBatchService, DarkFeatureManager darkFeatureManager, UserAccessor userAccessor, VelocityHelperService velocityHelperService) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver);
        this.jiraIssuesManager = jiraIssuesManager;
        this.settingsManager = settingsManager;
        this.jiraIssuesColumnManager = jiraIssuesColumnManager;
        this.trustedApplicationConfig = trustedApplicationConfig;
        this.permissionManager = permissionManager;
        this.applicationLinkResolver = applicationLinkResolver;
        this.macroMarshallingFactory = macroMarshallingFactory;
        this.jiraCacheManager = jiraCacheManager;
        this.imagePlaceHolderHelper = imagePlaceHolderHelper;
        this.formatSettingsManager = formatSettingsManager;
        this.jiraIssueSortingManager = jiraIssueSortingManager;
        this.jiraExceptionHelper = jiraExceptionHelper;
        this.localeManager = (LocaleManager)Preconditions.checkNotNull((Object)localeManager);
        this.asyncJiraIssueBatchService = asyncJiraIssueBatchService;
        this.darkFeatureManager = darkFeatureManager;
        this.velocityHelperService = velocityHelperService;
        this.xmlXformer = new JiraIssuesXmlTransformer(this.jiraIssuesManager);
        this.userAccessor = userAccessor;
    }

    protected I18nResolver getI18nResolver() {
        return this.i18nResolver;
    }

    String getText(String i18n, Object ... params) {
        return this.i18nResolver.getText(i18n, new Serializable[]{params});
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        String tokenTypeString = (String)parameters.get(TOKEN_TYPE_PARAM);
        if (StringUtils.isBlank((CharSequence)tokenTypeString)) {
            return TokenType.INLINE_BLOCK;
        }
        for (TokenType value : TokenType.values()) {
            if (!value.toString().equals(tokenTypeString)) continue;
            return TokenType.valueOf((String)tokenTypeString);
        }
        return TokenType.INLINE_BLOCK;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext conversionContext) {
        try {
            JiraRequestData jiraRequestData = JiraIssueUtil.parseRequestData(parameters, this.i18nResolver);
            return this.imagePlaceHolderHelper.getJiraMacroImagePlaceholder(jiraRequestData, parameters, this.resourcePath);
        }
        catch (MacroExecutionException e) {
            LOGGER.error("Error generate macro placeholder", (Throwable)e);
            return null;
        }
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    private boolean isTrustWarningsEnabled() {
        return null != this.trustedApplicationConfig && this.trustedApplicationConfig.isTrustWarningsEnabled();
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)params, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    protected void createContextMapFromParams(Map<String, String> params, Map<String, Object> contextMap, String requestData, Type requestType, ReadOnlyApplicationLink applink, boolean staticMode, boolean isMobile, JiraIssuesType issuesType, ConversionContext conversionContext) throws MacroExecutionException, ExecutionException {
        boolean userAuthenticated;
        boolean forceAnonymous;
        String showSummaryParam;
        Object width;
        String forceTrustWarningsStr;
        String anonymousStr;
        int maximumIssues;
        String columnIdsString = JiraUtil.getParamValue(params, COLUMN_IDS, 1);
        this.jiraIssuesManager.setIdParamPresent(StringUtils.isNotEmpty((CharSequence)columnIdsString));
        Set<String> columns = JiraUtil.getColumnNamesFromParams(params, true);
        int n = maximumIssues = staticMode ? JiraUtil.getMaximumIssues(params.get(MAXIMUM_ISSUES)) : 20;
        if (issuesType == JiraIssuesType.COUNT) {
            maximumIssues = 0;
        }
        contextMap.put(MAX_ISSUES_TO_DISPLAY, maximumIssues);
        String clickableUrl = JiraIssueUtil.getClickableUrl(requestData, requestType, applink, params.get(BASE_URL));
        contextMap.put(CLICKABLE_URL, clickableUrl);
        Set<JiraColumnInfo> jiraColumns = this.jiraIssuesColumnManager.getColumnsInfoFromJira(applink);
        Set<JiraColumnInfo> jiraColumnsFiltered = this.jiraIssuesColumnManager.getColumnInfo(params, jiraColumns, applink);
        contextMap.put(COLUMNS, jiraColumnsFiltered);
        if (issuesType == JiraIssuesType.TABLE) {
            requestData = this.jiraIssueSortingManager.getRequestDataForSorting(params, requestData, requestType, jiraColumns, conversionContext, applink);
        }
        String url = null;
        if (applink != null) {
            url = this.getXmlUrl(maximumIssues, requestData, requestType, applink);
        } else if (requestType == Type.URL) {
            url = requestData;
        }
        if (url == null && applink == null) {
            throw new MacroExecutionException(this.getText("jiraissues.error.noapplinks", new Object[0]));
        }
        if (issuesType == JiraIssuesType.SINGLE) {
            contextMap.put(KEY, this.getKeyFromRequest(requestData, requestType));
        }
        params.put(TOKEN_TYPE_PARAM, issuesType == JiraIssuesType.COUNT || requestType == Type.KEY ? TokenType.INLINE.name() : TokenType.BLOCK.name());
        String cacheParameter = JiraUtil.getParamValue(params, CACHE, 2);
        if ("pdf".equals(conversionContext.getOutputType())) {
            contextMap.put(PDF_EXPORT, Boolean.TRUE);
            JiraIssuePdfExportUtil.addedHelperDataForPdfExport(contextMap, columns != null ? columns.size() : 0);
        }
        if (params.containsKey(TITLE)) {
            contextMap.put(TITLE, HtmlUtil.htmlEncode((String)params.get(TITLE)));
        }
        if (EMAIL_RENDER.equals(conversionContext.getOutputType())) {
            contextMap.put(EMAIL_RENDER, Boolean.TRUE);
        }
        if ("".equals(anonymousStr = JiraUtil.getParamValue(params, ANONYMOUS, 4))) {
            anonymousStr = "false";
        }
        if ("".equals(forceTrustWarningsStr = JiraUtil.getParamValue(params, "forceTrustWarnings", 5))) {
            forceTrustWarningsStr = "false";
        }
        if ((width = params.get(WIDTH)) == null) {
            width = DEFAULT_DATA_WIDTH;
        } else if (!((String)width).contains("%") && !((String)width).contains("px")) {
            width = (String)width + "px";
        }
        contextMap.put(WIDTH, width);
        String heightStr = JiraUtil.getParamValue(params, HEIGHT, 6);
        if (!StringUtils.isEmpty((CharSequence)heightStr) && StringUtils.isNumeric((CharSequence)heightStr)) {
            contextMap.put(HEIGHT, heightStr);
        }
        if (StringUtils.isEmpty((CharSequence)(showSummaryParam = JiraUtil.getParamValue(params, SHOW_SUMMARY, 7)))) {
            contextMap.put(SHOW_SUMMARY, true);
        } else {
            contextMap.put(SHOW_SUMMARY, Boolean.parseBoolean(showSummaryParam));
        }
        boolean bl = forceAnonymous = Boolean.valueOf(anonymousStr) != false || requestType == Type.URL && SeraphUtils.isUserNamePasswordProvided(requestData);
        if (applink == null) {
            forceAnonymous = true;
        }
        boolean showTrustWarnings = Boolean.valueOf(forceTrustWarningsStr) != false || this.isTrustWarningsEnabled();
        contextMap.put("showTrustWarnings", showTrustWarnings);
        boolean isAdministrator = this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
        contextMap.put(IS_ADMINISTRATOR, isAdministrator);
        contextMap.put(IS_SOURCE_APP_LINK, applink != null);
        contextMap.put("returnMax", "true");
        boolean bl2 = userAuthenticated = AuthenticatedUserThreadLocal.get() != null;
        boolean useCache = JiraIssuesType.TABLE.equals((Object)issuesType) && !JiraJqlHelper.isJqlKeyType(requestData) ? StringUtils.isBlank((CharSequence)cacheParameter) || cacheParameter.equals("on") || Boolean.valueOf(cacheParameter) != false : !userAuthenticated || forceAnonymous;
        boolean doCacheResponse = JiraIssuesType.COUNT.equals((Object)issuesType);
        if (staticMode || isMobile) {
            switch (issuesType) {
                case SINGLE: {
                    if (EMAIL_RENDER.equals(conversionContext.getOutputDeviceType()) || EMAIL_RENDER.equals(conversionContext.getOutputType())) {
                        contextMap.put(IS_NO_PERMISSION_TO_VIEW, true);
                        break;
                    }
                    this.populateContextMapForStaticSingleIssue(contextMap, url, applink, forceAnonymous, useCache, conversionContext);
                    break;
                }
                case COUNT: {
                    this.populateContextMapForStaticCountIssues(params, contextMap, new LinkedHashSet<String>(), url, applink, forceAnonymous, useCache, conversionContext, doCacheResponse);
                    break;
                }
                case TABLE: {
                    contextMap.put("singleIssueTable", JiraJqlHelper.isJqlKeyType(requestData));
                    this.populateContextMapForStaticTable(params, contextMap, jiraColumnsFiltered, columns, url, applink, forceAnonymous, useCache, conversionContext, jiraColumns);
                    break;
                }
            }
        } else {
            if (applink != null) {
                contextMap.put("applink", applink);
            }
            if (issuesType != JiraIssuesType.SINGLE) {
                this.populateContextMapForDynamicTable(params, contextMap, columns, useCache, url, applink, forceAnonymous);
            }
        }
        if (issuesType == JiraIssuesType.TABLE) {
            this.registerTableRefreshContext(params, contextMap, conversionContext);
        }
    }

    public void registerTableRefreshContext(Map<String, String> macroParams, Map<String, Object> contextMap, ConversionContext conversionContext) throws MacroExecutionException {
        int refreshId = this.getNextRefreshId();
        contextMap.put("refreshId", refreshId);
        MacroDefinition macroDefinition = MacroDefinition.builder((String)JIRA).withMacroBody((MacroBody)new RichTextMacroBody("")).withParameters(macroParams).build();
        try {
            Streamable out = this.macroMarshallingFactory.getStorageMarshaller().marshal((Object)macroDefinition, conversionContext);
            StringWriter writer = new StringWriter();
            out.writeTo((Writer)writer);
            contextMap.put("wikiMarkup", writer.toString());
        }
        catch (XhtmlException | IOException e) {
            throw new MacroExecutionException("Unable to constract macro definition.", e);
        }
        String contentId = conversionContext.getEntity() != null ? conversionContext.getEntity().getIdAsString() : "-1";
        contextMap.put("contentId", contentId);
    }

    private String getKeyFromRequest(String requestData, Type requestType) {
        String key = requestData;
        if (requestType == Type.URL) {
            key = JiraJqlHelper.getKeyFromURL(requestData);
        }
        return key;
    }

    private String getRenderedTemplateMobile(Map<String, Object> contextMap, JiraIssuesType issuesType) {
        switch (issuesType) {
            case SINGLE: {
                return this.velocityHelperService.getRenderedTemplate("templates/mobile/extra/jira/mobileSingleJiraIssue.vm", contextMap);
            }
            case COUNT: {
                return this.velocityHelperService.getRenderedTemplate("templates/mobile/extra/jira/mobileShowCountJiraissues.vm", contextMap);
            }
        }
        return this.velocityHelperService.getRenderedTemplate("templates/mobile/extra/jira/mobileJiraIssues.vm", contextMap);
    }

    public String getRenderedTemplate(Map<String, Object> contextMap, boolean staticMode, JiraIssuesType issuesType) {
        if (staticMode) {
            return this.renderStaticTemplate(contextMap, issuesType);
        }
        return this.renderDynamicTemplate(contextMap, issuesType);
    }

    private String renderStaticTemplate(Map<String, Object> contextMap, JiraIssuesType issuesType) {
        switch (issuesType) {
            case SINGLE: {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/staticsinglejiraissue.vm", contextMap);
            }
            case COUNT: {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/staticShowCountJiraissues.vm", contextMap);
            }
        }
        return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/staticJiraIssues.vm", contextMap);
    }

    private String renderDynamicTemplate(Map<String, Object> contextMap, JiraIssuesType issuesType) {
        switch (issuesType) {
            case SINGLE: {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/singlejiraissue.vm", contextMap);
            }
            case COUNT: {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/showCountJiraissues.vm", contextMap);
            }
        }
        return this.velocityHelperService.getRenderedTemplate("templates/extra/jira/dynamicJiraIssues.vm", contextMap);
    }

    private void populateContextMapForStaticSingleIssue(Map<String, Object> contextMap, String url, ReadOnlyApplicationLink applicationLink, boolean forceAnonymous, boolean useCache, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, DEFAULT_COLUMNS_FOR_SINGLE_ISSUE, applicationLink, forceAnonymous, useCache);
            this.setupContextMapForStaticSingleIssue(contextMap, channel.getChannelElement().getChild(ITEM), applicationLink);
        }
        catch (CredentialsRequiredException credentialsRequiredException) {
            try {
                this.populateContextMapForStaticSingleIssueAnonymous(contextMap, url, applicationLink, forceAnonymous, useCache, conversionContext);
            }
            catch (MacroExecutionException e) {
                contextMap.put("oAuthUrl", credentialsRequiredException.getAuthorisationURI().toString());
            }
        }
        catch (Exception e) {
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
    }

    private void populateContextMapForStaticSingleIssueAnonymous(Map<String, Object> contextMap, String url, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean useCache, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannelByAnonymous(url, DEFAULT_COLUMNS_FOR_SINGLE_ISSUE, applink, forceAnonymous, useCache);
            this.setupContextMapForStaticSingleIssue(contextMap, channel.getChannelElement().getChild(ITEM), applink);
        }
        catch (Exception e) {
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
    }

    private void setupContextMapForStaticSingleIssue(Map<String, Object> contextMap, Element issue, ReadOnlyApplicationLink applicationLink) throws MalformedRequestException {
        Element statusCategory;
        if (issue == null) {
            if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
                throw new MalformedRequestException();
            }
            throw new JiraIssueDataException();
        }
        Element resolution = issue.getChild("resolution");
        Element status = issue.getChild("status");
        JiraUtil.checkAndCorrectIconURL(issue, applicationLink);
        contextMap.put("resolved", resolution != null && !"-1".equals(resolution.getAttributeValue("id")));
        contextMap.put(ICON_URL, issue.getChild("type").getAttributeValue(ICON_URL));
        String key = issue.getChild(KEY).getValue();
        contextMap.put(KEY, key);
        contextMap.put("summary", issue.getChild("summary").getValue());
        contextMap.put("status", status.getValue());
        contextMap.put("statusIcon", status.getAttributeValue(ICON_URL));
        Element isPlaceholder = issue.getChild("isPlaceholder");
        contextMap.put("isPlaceholder", isPlaceholder != null);
        Element clientIdElement = issue.getChild(CLIENT_ID);
        if (clientIdElement != null) {
            contextMap.put(CLIENT_ID, clientIdElement.getValue());
        }
        if (null != (statusCategory = issue.getChild("statusCategory"))) {
            String colorName = statusCategory.getAttribute("colorName").getValue();
            String keyName = statusCategory.getAttribute(KEY).getValue();
            if (StringUtils.isNotBlank((CharSequence)colorName) && StringUtils.isNotBlank((CharSequence)keyName)) {
                contextMap.put("statusColor", colorName);
                contextMap.put("keyName", keyName);
            }
        }
    }

    private String getXmlUrl(int maximumIssues, String requestData, Type requestType, ReadOnlyApplicationLink applicationLink) throws MacroExecutionException {
        StringBuilder stringBuilder = new StringBuilder(JiraUtil.normalizeUrl(applicationLink.getRpcUrl()));
        stringBuilder.append("/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml").append("?tempMax=").append(maximumIssues).append("&returnMax=true&jqlQuery=");
        switch (requestType) {
            case URL: {
                if (JiraJqlHelper.isUrlFilterType(requestData)) {
                    String jql = JiraJqlHelper.getJQLFromFilter(applicationLink, requestData, this.jiraIssuesManager, this.i18nResolver);
                    stringBuilder.append(JiraUtil.utf8Encode(jql));
                    return stringBuilder.toString();
                }
                if (requestData.contains("searchrequest-xml")) {
                    return requestData.trim();
                }
                String jql = JiraJqlHelper.getJQLFromJQLURL(requestData);
                if (jql != null) {
                    stringBuilder.append(JiraUtil.utf8Encode(jql));
                    return stringBuilder.toString();
                }
                if (JiraJqlHelper.isUrlKeyType(requestData)) {
                    String key = JiraJqlHelper.getKeyFromURL(requestData);
                    return this.buildKeyJiraUrl(key, applicationLink);
                }
            }
            case JQL: {
                stringBuilder.append(JiraUtil.utf8Encode(requestData));
                return stringBuilder.toString();
            }
            case KEY: {
                return this.buildKeyJiraUrl(requestData, applicationLink);
            }
        }
        throw new MacroExecutionException("Invalid url");
    }

    private String buildKeyJiraUrl(String key, ReadOnlyApplicationLink applicationLink) {
        String encodedQuery = JiraUtil.utf8Encode("key in (" + key + ")");
        return JiraUtil.normalizeUrl(applicationLink.getRpcUrl()) + "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=" + encodedQuery + "&returnMax=true";
    }

    private String executeRest(String restUrl, ReadOnlyApplicationLink appLink) {
        String json = "";
        try {
            ApplicationLinkRequest fieldRequest = JiraConnectorUtils.getApplicationLinkRequest(appLink, Request.MethodType.GET, restUrl);
            fieldRequest.addHeader("Content-Type", "application/json");
            json = fieldRequest.execute();
        }
        catch (CredentialsRequiredException e) {
            LOGGER.error("CredentialsRequiredException", (Throwable)e);
        }
        catch (ResponseException e) {
            LOGGER.error("ResponseExceptionException", (Throwable)e);
        }
        return json;
    }

    private Map<String, String> getEpicSchemaIds(Set<JiraColumnInfo> jiraColumnInfos) {
        HashMap<String, String> epicSchemaIds = new HashMap<String, String>();
        for (JiraColumnInfo column : jiraColumnInfos) {
            if (DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.pyxis.greenhopper.jira:gh-epic-label", column)) {
                LOGGER.debug("Epic name column detected");
                epicSchemaIds.put("com.pyxis.greenhopper.jira:gh-epic-label", column.getKey());
            } else if (DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.pyxis.greenhopper.jira:gh-epic-color", column)) {
                LOGGER.debug("Epic colour column detected");
                epicSchemaIds.put("com.pyxis.greenhopper.jira:gh-epic-color", column.getKey());
            } else if (DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.pyxis.greenhopper.jira:gh-epic-status", column)) {
                LOGGER.debug("Epic status column detected");
                epicSchemaIds.put("com.pyxis.greenhopper.jira:gh-epic-status", column.getKey());
            }
            if (!epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-label") || !epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-color") || !epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-status")) continue;
            break;
        }
        return epicSchemaIds;
    }

    private void populateTableEpicData(Map<String, Object> contextMap, ReadOnlyApplicationLink appLink, Channel channel, Set<JiraColumnInfo> jiraColumnInfos) {
        Map<String, String> epicSchemaIds = this.getEpicSchemaIds(jiraColumnInfos);
        if (!(epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-label") && epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-color") && epicSchemaIds.containsKey("com.pyxis.greenhopper.jira:gh-epic-status"))) {
            LOGGER.debug("Epic field ids empty");
            contextMap.put("issueKeyToEpic", new HashMap());
            return;
        }
        String epicNameCustomFieldId = epicSchemaIds.get("com.pyxis.greenhopper.jira:gh-epic-label");
        String epicColourCustomFieldId = epicSchemaIds.get("com.pyxis.greenhopper.jira:gh-epic-color");
        String epicStatusCustomFieldId = epicSchemaIds.get("com.pyxis.greenhopper.jira:gh-epic-status");
        DefaultEpicInfoRetriever retriever = new DefaultEpicInfoRetriever(appLink, new RestHelper(), epicNameCustomFieldId, epicColourCustomFieldId, epicStatusCustomFieldId);
        EpicInformationHelper epicInformationHelper = new EpicInformationHelper(retriever, epicNameCustomFieldId, epicColourCustomFieldId, epicStatusCustomFieldId);
        contextMap.put("issueKeyToEpic", epicInformationHelper.getEpicInformation(channel));
    }

    private void populateTeamNameData(Map<String, Object> contextMap, ReadOnlyApplicationLink appLink, Channel channel, Set<JiraColumnInfo> jiraColumnInfos) {
        String teamNameCustomFieldId = "";
        for (JiraColumnInfo column : jiraColumnInfos) {
            if (!DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.atlassian.teams:rm-teams-custom-field-team", column)) continue;
            teamNameCustomFieldId = column.getKey();
            break;
        }
        if (teamNameCustomFieldId.isEmpty()) {
            contextMap.put("teamIdToTeamNames", new HashMap());
            return;
        }
        contextMap.put("teamIdToTeamNames", this.getTeamInformation(channel, appLink, teamNameCustomFieldId));
    }

    private Map<String, Team> getTeamInformation(Channel channel, ReadOnlyApplicationLink appLink, String teamNameCustomFieldId) {
        HashSet<String> teamIds = new HashSet<String>();
        for (Element issue : channel.getChannelElement().getChildren(ITEM)) {
            String teamId = "";
            if (issue.getChild("customfields") != null) {
                for (Element element : issue.getChild("customfields").getChildren()) {
                    Attribute schema = element.getAttributes().stream().filter(attribute -> KEY.equals(((Attribute)attribute).getName())).findFirst().orElse(null);
                    Attribute id = element.getAttributes().stream().filter(attribute -> "id".equals(((Attribute)attribute).getName())).findFirst().orElse(null);
                    if (!DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.atlassian.teams:rm-teams-custom-field-team", new JiraColumnInfo(id.getValue(), null, null, false, new JiraColumnInfo.JsonSchema(null, schema.getValue(), 0, null)))) continue;
                    teamId = this.extractFieldValue(element.getValue());
                    break;
                }
            }
            if (teamId.isEmpty() || teamIds.contains(teamId)) continue;
            teamIds.add(teamId);
        }
        return this.getTeamsByIds(teamIds, appLink);
    }

    private Map<String, Team> getTeamsByIds(Set<String> teamIds, ReadOnlyApplicationLink appLink) {
        HashMap<String, Team> foundTeamIds = new HashMap<String, Team>();
        if (teamIds.size() == 0) {
            return foundTeamIds;
        }
        List params = teamIds.stream().map(id -> new BasicNameValuePair("ids", id)).collect(Collectors.toList());
        ArrayList teams = new ArrayList();
        AtomicInteger counter = new AtomicInteger(0);
        int size = 100;
        Gson gson = new Gson();
        Collection<List<NameValuePair>> partitions = params.stream().collect(Collectors.groupingBy(s -> counter.getAndIncrement() / 100)).values();
        partitions.forEach(partitionParams -> {
            String json = this.executeRest("/rest/teams-api/1.0/team?" + URLEncodedUtils.format((List)partitionParams, (String)"UTF8"), appLink);
            teams.addAll((Collection)gson.fromJson(json, new TypeToken<List<Team>>(){}.getType()));
        });
        return teams.stream().collect(Collectors.toMap(Team::getId, team -> team));
    }

    private String extractFieldValue(String field) {
        return field.trim().replaceAll(".*\n.*\n *", "");
    }

    private void populateContextMapForStaticTable(Map<String, String> macroParams, Map<String, Object> contextMap, Set<JiraColumnInfo> jiraColumnsFiltered, Set<String> columns, String url, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean useCache, ConversionContext conversionContext, Set<JiraColumnInfo> allJiraColumns) throws MacroExecutionException {
        boolean clearCache = this.getBooleanProperty(conversionContext.getProperty("clearCache"));
        try {
            boolean isViewingOrPreviewing = "display".equals(conversionContext.getOutputType()) || "preview".equals(conversionContext.getOutputType());
            contextMap.put(ENABLE_REFRESH, isViewingOrPreviewing);
            if (StringUtils.isNotBlank((CharSequence)((String)conversionContext.getProperty("orderColumnName"))) && StringUtils.isNotBlank((CharSequence)((String)conversionContext.getProperty("order")))) {
                contextMap.put("orderColumnName", conversionContext.getProperty("orderColumnName"));
                contextMap.put("order", conversionContext.getProperty("order"));
            }
            if (clearCache) {
                this.jiraCacheManager.clearJiraIssuesCache(url, columns, appLink, forceAnonymous, false);
            }
            boolean placeholder = isViewingOrPreviewing && this.isAsyncSupport(conversionContext);
            contextMap.put(PARAM_PLACEHOLDER, placeholder);
            if (!placeholder) {
                boolean containsEpicColumns = this.jiraIssuesColumnManager.columnsContainsEpicColumns(jiraColumnsFiltered);
                if (containsEpicColumns) {
                    Map<String, String> epicSchemaIds = this.getEpicSchemaIds(allJiraColumns);
                    for (String epicSchemaId : epicSchemaIds.values()) {
                        columns.add(epicSchemaId);
                    }
                }
                Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, columns, appLink, forceAnonymous, useCache);
                this.setupContextMapForStaticTable(contextMap, channel, appLink);
                if (containsEpicColumns) {
                    LOGGER.debug("Epic columns detected in Jira columns");
                    this.populateTableEpicData(contextMap, appLink, channel, allJiraColumns);
                }
                if (this.jiraIssuesColumnManager.columnsContainsTeamColumns(jiraColumnsFiltered)) {
                    this.populateTeamNameData(contextMap, appLink, channel, allJiraColumns);
                }
            } else {
                ClientId clientId = ClientId.fromElement(JiraIssuesType.TABLE, appLink.getId().get(), conversionContext.getEntity().getIdAsString(), JiraIssueUtil.getUserKey(AuthenticatedUserThreadLocal.get()), String.valueOf(macroParams.get("jqlQuery")), String.join((CharSequence)"", columns));
                contextMap.put(CLIENT_ID, clientId);
                this.asyncJiraIssueBatchService.processRequestWithJql(clientId, macroParams, conversionContext, appLink);
                contextMap.put("trustedConnection", false);
            }
        }
        catch (CredentialsRequiredException e) {
            if (clearCache) {
                this.jiraCacheManager.clearJiraIssuesCache(url, columns, appLink, forceAnonymous, true);
            }
            this.populateContextMapForStaticTableByAnonymous(contextMap, columns, url, appLink, forceAnonymous, useCache);
            contextMap.put("oAuthUrl", e.getAuthorisationURI().toString());
        }
        catch (MalformedRequestException e) {
            LOGGER.info("Can't get issues because issues key is not exist or user doesn't have permission to view", (Throwable)e);
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
        catch (Exception e) {
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
    }

    private void populateContextMapForStaticTableByAnonymous(Map<String, Object> contextMap, Set<String> columns, String url, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean useCache) {
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannelByAnonymous(url, columns, appLink, forceAnonymous, useCache);
            this.setupContextMapForStaticTable(contextMap, channel, appLink);
        }
        catch (Exception e) {
            contextMap.put(TOTAL_ISSUES, 0);
            LOGGER.info("Can't get jira issues by anonymous user from : " + appLink);
            LOGGER.debug("More info", (Throwable)e);
        }
    }

    public void setupContextMapForStaticTable(Map<String, Object> contextMap, Channel channel, ReadOnlyApplicationLink appLink) {
        Element element = channel.getChannelElement();
        contextMap.put("trustedConnection", channel.isTrustedConnection());
        contextMap.put("trustedConnectionStatus", channel.getTrustedConnectionStatus());
        contextMap.put("channel", element);
        contextMap.put("entries", element.getChildren(ITEM));
        JiraUtil.checkAndCorrectDisplayUrl(element.getChildren(ITEM), appLink);
        try {
            if (element.getChild("issue") != null && element.getChild("issue").getAttribute("total") != null) {
                contextMap.put(TOTAL_ISSUES, element.getChild("issue").getAttribute("total").getIntValue());
            }
        }
        catch (DataConversionException e) {
            contextMap.put(TOTAL_ISSUES, element.getChildren(ITEM).size());
        }
        contextMap.put("xmlXformer", this.xmlXformer);
        contextMap.put("jiraIssuesColumnManager", this.jiraIssuesColumnManager);
        if (null != appLink) {
            contextMap.put(JIRA_SERVER_URL, JiraUtil.normalizeUrl(appLink.getDisplayUrl()));
        } else {
            try {
                URL sourceUrl = new URL(channel.getSourceUrl());
                String jiraServerUrl = sourceUrl.getProtocol() + "://" + sourceUrl.getAuthority();
                contextMap.put(JIRA_SERVER_URL, jiraServerUrl);
            }
            catch (MalformedURLException e) {
                LOGGER.debug("MalformedURLException thrown when retrieving sourceURL from the channel", (Throwable)e);
                LOGGER.info("Set jiraServerUrl to empty string");
                contextMap.put(JIRA_SERVER_URL, "");
            }
        }
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        contextMap.put("userLocale", this.localeManager.getLocale((User)confluenceUser));
        contextMap.put("userZoneId", ZoneId.of(this.userAccessor.getConfluenceUserPreferences((User)confluenceUser).getTimeZone().getID()));
        contextMap.put("userDateFormat", this.formatSettingsManager.getDateFormat());
        contextMap.put("systemDecimalFormat", this.formatSettingsManager.getDecimalNumberFormat());
    }

    private void populateContextMapForStaticCountIssues(Map<String, String> macroParams, Map<String, Object> contextMap, Set<String> columns, String url, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean useCache, ConversionContext conversionContext, boolean doCacheResponse) throws MacroExecutionException {
        if (this.isAsyncSupport(conversionContext)) {
            ClientId clientId = ClientId.fromElement(JiraIssuesType.COUNT, appLink.getId().get(), conversionContext.getEntity().getIdAsString(), JiraIssueUtil.getUserKey(AuthenticatedUserThreadLocal.get()), String.valueOf(macroParams.get("jqlQuery")), columns == null || columns.size() == 0 ? null : String.join((CharSequence)"", columns));
            contextMap.put(CLIENT_ID, clientId);
            this.asyncJiraIssueBatchService.processRequestWithJql(clientId, macroParams, conversionContext, appLink);
        } else {
            try {
                Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, columns, appLink, forceAnonymous, useCache, doCacheResponse);
                Element element = channel.getChannelElement();
                Element totalItemsElement = element.getChild("issue");
                String count = totalItemsElement != null ? totalItemsElement.getAttributeValue("total") : "" + element.getChildren(ITEM).size();
                contextMap.put(COUNT, count);
            }
            catch (CredentialsRequiredException e) {
                contextMap.put(COUNT, this.getCountIssuesWithAnonymous(url, columns, appLink, forceAnonymous, useCache, doCacheResponse));
                contextMap.put("oAuthUrl", e.getAuthorisationURI().toString());
            }
            catch (MalformedRequestException e) {
                contextMap.put(COUNT, DEFAULT_JIRA_ISSUES_COUNT);
            }
            catch (Exception e) {
                this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
            }
        }
    }

    private String getCountIssuesWithAnonymous(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean useCache, boolean doCacheResponse) {
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannelByAnonymous(url, new LinkedHashSet<String>(), appLink, forceAnonymous, useCache, doCacheResponse);
            Element element = channel.getChannelElement();
            Element totalItemsElement = element.getChild("issue");
            return totalItemsElement != null ? totalItemsElement.getAttributeValue("total") : "" + element.getChildren(ITEM).size();
        }
        catch (Exception e) {
            LOGGER.info("Can not retrieve total issues by anonymous");
            return DEFAULT_JIRA_ISSUES_COUNT;
        }
    }

    private void populateContextMapForDynamicTable(Map<String, String> params, Map<String, Object> contextMap, Set<String> columns, boolean checkCacheBeforeLookup, String url, ReadOnlyApplicationLink applink, boolean forceAnonymous) throws MacroExecutionException {
        StringBuffer urlBuffer = new StringBuffer(url);
        contextMap.put("resultsPerPage", this.getResultsPerPageParam(urlBuffer));
        String startOn = this.getStartOnParam(params.get("startOn"), urlBuffer);
        contextMap.put("startOn", Integer.valueOf(startOn));
        contextMap.put("sortOrder", this.getSortOrderParam(urlBuffer));
        contextMap.put("sortField", this.getSortFieldParam(urlBuffer));
        contextMap.put("useCache", checkCacheBeforeLookup);
        contextMap.put("retrieverUrlHtml", this.buildRetrieverUrl(columns, urlBuffer.toString(), applink, forceAnonymous));
    }

    private String getStartOnParam(String startOn, StringBuffer urlParam) {
        String pagerStart = JiraIssueUtil.filterOutParam(urlParam, "pager/start=");
        if (StringUtils.isNotEmpty((CharSequence)startOn)) {
            return startOn.trim();
        }
        if (StringUtils.isNotEmpty((CharSequence)pagerStart)) {
            return pagerStart;
        }
        return DEFAULT_JIRA_ISSUES_COUNT;
    }

    private String getSortOrderParam(StringBuffer urlBuffer) {
        String sortOrder = JiraIssueUtil.filterOutParam(urlBuffer, "sorter/order=");
        if (StringUtils.isNotEmpty((CharSequence)sortOrder)) {
            return sortOrder.toLowerCase();
        }
        return "desc";
    }

    private String getSortFieldParam(StringBuffer urlBuffer) {
        String sortField = JiraIssueUtil.filterOutParam(urlBuffer, "sorter/field=");
        if (StringUtils.isNotEmpty((CharSequence)sortField)) {
            return sortField;
        }
        return null;
    }

    private boolean shouldRenderInHtml(String renderModeParamValue, ConversionContext conversionContext) {
        return "pdf".equals(conversionContext.getOutputType()) || "word".equals(conversionContext.getOutputType()) || !DYNAMIC_RENDER_MODE.equals(renderModeParamValue) || EMAIL_RENDER.equals(conversionContext.getOutputType()) || "feed".equals(conversionContext.getOutputType()) || "html_export".equals(conversionContext.getOutputType());
    }

    protected boolean isAsyncSupport(ConversionContext conversionContext) {
        ContentEntityObject entity = conversionContext.getEntity();
        return entity != null && this.getBooleanProperty(conversionContext.getProperty(PARAM_PLACEHOLDER, (Object)true)) && !this.darkFeatureManager.isFeatureEnabledForCurrentUser("confluence.extra.jira.async.loading.disable") && "display".equals(conversionContext.getOutputType()) && "desktop".equals(conversionContext.getOutputDeviceType()) && (entity.getTypeEnum() == ContentTypeEnum.BLOG || entity.getTypeEnum() == ContentTypeEnum.PAGE || entity.getTypeEnum() == ContentTypeEnum.COMMENT);
    }

    protected int getResultsPerPageParam(StringBuffer urlParam) throws MacroExecutionException {
        String tempMaxParam = JiraIssueUtil.filterOutParam(urlParam, "tempMax=");
        if (StringUtils.isNotEmpty((CharSequence)tempMaxParam)) {
            int tempMax = Integer.parseInt(tempMaxParam);
            if (tempMax <= 0) {
                throw new MacroExecutionException("The tempMax parameter in the Jira url must be greater than zero.");
            }
            return tempMax;
        }
        return 10;
    }

    private String buildRetrieverUrl(Set<String> columns, String url, ReadOnlyApplicationLink applicationLink, boolean forceAnonymous) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        StringBuilder retrieverUrl = new StringBuilder(baseUrl);
        retrieverUrl.append("/plugins/servlet/issue-retriever?");
        retrieverUrl.append("url=").append(JiraUtil.utf8Encode(url));
        if (applicationLink != null) {
            retrieverUrl.append("&appId=").append(JiraUtil.utf8Encode(applicationLink.getId().toString()));
        }
        for (String column : columns) {
            retrieverUrl.append("&columns=").append(JiraUtil.utf8Encode(column));
        }
        retrieverUrl.append("&forceAnonymous=").append(forceAnonymous);
        retrieverUrl.append("&flexigrid=true");
        return retrieverUrl.toString();
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        Map contextMap = null;
        try {
            JiraRequestData jiraRequestData = JiraIssueUtil.parseRequestData(parameters, this.i18nResolver);
            String requestData = jiraRequestData.getRequestData();
            Type requestType = jiraRequestData.getRequestType();
            contextMap = this.velocityHelperService.createDefaultVelocityContext();
            JiraIssuesType issuesType = JiraUtil.getJiraIssuesType(parameters, requestType, requestData);
            contextMap.put(ISSUE_TYPE, issuesType);
            ImmutableMap<String, ImmutableSet<String>> i18nColumnNames = this.jiraIssuesColumnManager.getI18nColumnNames();
            contextMap.put("i18nColumnNames", i18nColumnNames);
            Set<String> columnNames = JiraUtil.getColumnNamesFromParams(parameters, false);
            contextMap.put(COLUMNS, columnNames);
            ReadOnlyApplicationLink applink = null;
            try {
                applink = this.applicationLinkResolver.resolve(requestType, requestData, parameters);
            }
            catch (TypeNotInstalledException tne) {
                this.jiraExceptionHelper.throwMacroExecutionException((Exception)((Object)tne), conversionContext);
            }
            boolean staticMode = !this.dynamicRenderModeEnabled(parameters, conversionContext);
            boolean isMobile = MOBILE.equals(conversionContext.getOutputDeviceType());
            this.createContextMapFromParams(parameters, contextMap, requestData, requestType, applink, staticMode, isMobile, issuesType, conversionContext);
            if (isMobile) {
                return this.getRenderedTemplateMobile(contextMap, issuesType);
            }
            return this.getRenderedTemplate(contextMap, staticMode, issuesType);
        }
        catch (Exception e) {
            throw new JiraIssueMacroException(e, contextMap);
        }
    }

    protected boolean dynamicRenderModeEnabled(Map<String, String> parameters, ConversionContext conversionContext) {
        return !this.shouldRenderInHtml(parameters.get(RENDER_MODE_PARAM), conversionContext);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public JiraIssuesXmlTransformer getXmlXformer() {
        return this.xmlXformer;
    }

    private int getNextRefreshId() {
        return RANDOM.nextInt();
    }

    private boolean getBooleanProperty(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            return BooleanUtils.toBoolean((String)((String)value));
        }
        return false;
    }

    private void setRenderMode(Map<String, Object> contextMap, String outputType) {
        if ("pdf".equals(outputType)) {
            contextMap.put(PDF_EXPORT, Boolean.TRUE);
        }
        if (EMAIL_RENDER.equals(outputType)) {
            contextMap.put(EMAIL_RENDER, Boolean.TRUE);
        }
    }

    public String renderSingleJiraIssue(Map<String, String> parameters, ConversionContext conversionContext, Element issue, String displayUrl, String rpcUrl) throws Exception {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        String outputType = conversionContext.getOutputType();
        this.setRenderMode(contextMap, outputType);
        String showSummaryParam = JiraUtil.getParamValue(parameters, SHOW_SUMMARY, 7);
        if (StringUtils.isEmpty((CharSequence)showSummaryParam)) {
            contextMap.put(SHOW_SUMMARY, true);
        } else {
            contextMap.put(SHOW_SUMMARY, Boolean.parseBoolean(showSummaryParam));
        }
        JiraUtil.correctIconURL(issue, displayUrl, rpcUrl);
        this.setupContextMapForStaticSingleIssue(contextMap, issue, null);
        contextMap.put(CLICKABLE_URL, displayUrl + JIRA_BROWSE_URL + issue.getChild(KEY).getValue());
        boolean isMobile = MOBILE.equals(conversionContext.getOutputDeviceType());
        if (isMobile) {
            return this.getRenderedTemplateMobile(contextMap, JiraIssuesType.SINGLE);
        }
        return this.getRenderedTemplate(contextMap, true, JiraIssuesType.SINGLE);
    }

    public static enum JiraIssuesType {
        SINGLE,
        COUNT,
        TABLE;

    }

    public static enum Type {
        KEY,
        JQL,
        URL;

    }
}

