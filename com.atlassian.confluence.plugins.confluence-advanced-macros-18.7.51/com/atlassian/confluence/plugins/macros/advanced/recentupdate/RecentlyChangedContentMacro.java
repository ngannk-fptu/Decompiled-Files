/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.ContentFilteringMacro
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.params.ParameterException
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchWithToken
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.actions.ContentTypesDisplayMapper
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.ContentFilteringMacro;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultGrouper;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultUpdateItemFactory;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.RecentChangesSearchBuilder;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentlyChangedContentMacro
extends ContentFilteringMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(RecentlyChangedContentMacro.class);
    private static final String MACRO_NAME = "recently-updated";
    private UserAccessor userAccessor;
    private FormatSettingsManager formatSettingsManager;
    private LocaleManager localeManager;
    private I18NBeanFactory i18NBeanFactory;
    private ContentTypesDisplayMapper contentTypesDisplayMapper;
    private ContextPathHolder contextPathHolder;
    private PluginAccessor pluginAccessor;
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    public RecentlyChangedContentMacro() {
        this.contentTypeParam.addParameterAlias("types");
        this.contentTypeParam.setDefaultValue(RecentlyChangedContentMacro.getDefaultTypeParamValue());
        this.spaceKeyParam.setDefaultValue("@self");
        this.maxResultsParam.setDefaultValue("15");
    }

    protected String execute(MacroExecutionContext macroExecutionContext) throws MacroException {
        try {
            return this.execute(macroExecutionContext.getParams(), macroExecutionContext.getBody(), (ConversionContext)new DefaultConversionContext((RenderContext)macroExecutionContext.getPageContext()));
        }
        catch (MacroExecutionException ex) {
            throw new MacroException(ex.getCause() != null ? ex.getCause() : ex);
        }
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        SearchResults searchResults;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)user));
        String authorParamValue = this.authorParam.getParameterValue(parameters);
        String labelParamValue = this.labelParam.getParameterValue(parameters);
        String contentTypeParamValue = this.contentTypeParam.getParameterValue(parameters);
        String spaceKeyParamValue = this.getSpaceKeyParamValue(parameters, conversionContext);
        Theme theme = this.getTheme(parameters);
        int maxResults = this.getMaxResults(new MacroExecutionContext(parameters, body, conversionContext.getPageContext()), i18n);
        boolean hideHeading = RecentlyChangedContentMacro.getHideHeading(parameters);
        String paramFilter = parameters.get("filter");
        String paramWidth = parameters.get("width");
        RecentChangesSearchBuilder searchBuilder = new RecentChangesSearchBuilder(this.pluginAccessor, this.userAccessor, this.siteSearchPermissionsQueryFactory);
        searchBuilder.withLabels(labelParamValue).withAuthors(authorParamValue).withContentTypes(contentTypeParamValue).withSpaceKeys(spaceKeyParamValue).withPageSize(maxResults);
        Map<String, Object> macroRenderContext = this.getMacroVelocityContext();
        if (StringUtils.isNotBlank((CharSequence)paramWidth)) {
            macroRenderContext.put("width", paramWidth);
        }
        ISearch changesSearch = searchBuilder.buildSearch();
        try {
            searchResults = this.searchManager.search(changesSearch);
        }
        catch (InvalidSearchException e) {
            throw new MacroExecutionException((Throwable)e);
        }
        boolean noResults = Iterables.isEmpty((Iterable)searchResults);
        if (!hideHeading || noResults) {
            macroRenderContext.put("title", i18n.getText("recently.updated"));
            macroRenderContext.put("titleHeadingId", conversionContext.getPageContext().getElementIdCreator().generateId("recently-updated-macro"));
        }
        if (noResults) {
            return this.renderEmptyTemplate(macroRenderContext);
        }
        ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = pref.getDateFormatter(this.formatSettingsManager, this.localeManager);
        DefaultUpdateItemFactory updateItemFactory = new DefaultUpdateItemFactory(dateFormatter, i18n, this.contentTypesDisplayMapper, this.pluginAccessor);
        macroRenderContext.put("changesUrl", searchBuilder.buildSearchUrl(theme, this.contextPathHolder.getContextPath()));
        if (StringUtils.isNotBlank((CharSequence)paramFilter)) {
            if (StringUtils.isNotBlank((CharSequence)parameters.get("type")) || StringUtils.isNotBlank((CharSequence)parameters.get("types"))) {
                throw new MacroExecutionException("Filter control is only supported when no type/types parameter is specified.");
            }
            macroRenderContext.put("filter", paramFilter);
        }
        macroRenderContext.put("contentTypes", RecentlyChangedContentMacro.getContentTypes());
        if (!searchResults.isLastPage()) {
            SearchWithToken nextPageSearch = searchResults.getNextPageSearch();
            searchBuilder.withStartIndex(nextPageSearch.getStartOffset()).withPageSize(nextPageSearch.getLimit()).withSearchToken(nextPageSearch.getSearchToken());
            macroRenderContext.put("nextPageUrl", searchBuilder.buildSearchUrl(theme, this.contextPathHolder.getContextPath()));
        }
        if ("html_export".equals(conversionContext.getOutputType())) {
            macroRenderContext.put("performingHtmlExport", true);
        }
        LinkedList<UpdateItem> updateItems = new LinkedList<UpdateItem>();
        for (Object searchResult : searchResults) {
            UpdateItem updateItem = updateItemFactory.get((SearchResult)searchResult);
            if (updateItem == null) continue;
            updateItems.add(updateItem);
        }
        if (Theme.social == theme) {
            DefaultGrouper grouper = new DefaultGrouper();
            for (UpdateItem updateItem : updateItems) {
                grouper.addUpdateItem(updateItem);
            }
            macroRenderContext.put("groupings", grouper.getUpdateItemGroupings());
        } else {
            macroRenderContext.put("updateItems", updateItems);
        }
        macroRenderContext.put("i18n", i18n);
        boolean mobile = false;
        if ("mobile".equals(conversionContext.getOutputDeviceType())) {
            mobile = true;
        }
        macroRenderContext.put("mobile", mobile);
        try {
            return this.renderRecentlyUpdated(theme, macroRenderContext);
        }
        catch (Exception e) {
            log.error("Error while trying to render the recently-updated template.", (Throwable)e);
            throw new MacroExecutionException(e.getMessage());
        }
    }

    protected String renderRecentlyUpdated(Theme theme, Map<String, Object> macroRenderContext) {
        return VelocityUtils.getRenderedTemplate((String)RecentlyChangedContentMacro.getTemplate(theme), macroRenderContext);
    }

    protected String renderEmptyTemplate(Map<String, Object> macroRenderContext) {
        String emptyTemplate = "com/atlassian/confluence/plugins/macros/advanced/recentupdate/no-updates.vm";
        return VelocityUtils.getRenderedTemplate((String)"com/atlassian/confluence/plugins/macros/advanced/recentupdate/no-updates.vm", macroRenderContext);
    }

    protected Map<String, Object> getMacroVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    private String getSpaceKeyParamValue(Map<String, String> params, ConversionContext conversionContext) {
        String value = this.spaceKeyParam.getParameterValue(params);
        if ("@self".equals(value) && conversionContext != null) {
            return conversionContext.getSpaceKey();
        }
        return value;
    }

    private Integer getMaxResults(MacroExecutionContext ctx, I18NBean i18n) throws MacroExecutionException {
        try {
            return (Integer)this.maxResultsParam.findValue(ctx);
        }
        catch (ParameterException pe) {
            throw new MacroExecutionException(i18n.getText("recently.updated.error.parse-max-results-param"));
        }
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    private Theme getTheme(Map<String, String> params) {
        if (Theme.social.name().equals(params.get("theme")) || "true".equalsIgnoreCase(params.get("showProfilePic"))) {
            return Theme.social;
        }
        if (Theme.sidebar.name().equals(params.get("theme"))) {
            return Theme.sidebar;
        }
        return Theme.concise;
    }

    private static String getTemplate(Theme theme) {
        return String.format("com/atlassian/confluence/plugins/macros/advanced/recentupdate/themes/%s/macro-template.vm", theme.name());
    }

    private static boolean getHideHeading(Map<String, String> params) {
        String value = params.get("hideHeading");
        return "true".equalsIgnoreCase(value);
    }

    public final String getName() {
        return MACRO_NAME;
    }

    private static String getDefaultTypeParamValue() {
        StringBuilder value = new StringBuilder();
        EnumSet<ContentTypeEnum[]> types = EnumSet.of(ContentTypeEnum.ATTACHMENT, new ContentTypeEnum[]{ContentTypeEnum.BLOG, ContentTypeEnum.COMMENT, ContentTypeEnum.CUSTOM, ContentTypeEnum.DRAFT, ContentTypeEnum.PAGE, ContentTypeEnum.PERSONAL_INFORMATION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION, ContentTypeEnum.SPACE, ContentTypeEnum.SPACE_DESCRIPTION});
        for (ContentTypeEnum contentTypeEnum : types) {
            value.append(",").append(contentTypeEnum.getRepresentation());
        }
        return value.toString();
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private static Map<String, String> getContentTypes() {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        result.put("page", "content.type.page");
        result.put("blogpost", "content.type.blogpost");
        result.put("comment", "content.type.comment");
        result.put("attachment", "content.type.attachment");
        return result;
    }

    public void setContentTypesDisplayMapper(ContentTypesDisplayMapper contentTypesDisplayMapper) {
        this.contentTypesDisplayMapper = contentTypesDisplayMapper;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setContextPathHolder(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }
}

