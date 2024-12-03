/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.query.BooleanQueryFactory
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.content_report;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ContentReportTableMacro
implements Macro {
    private static final String TEMPLATE_PROVIDER_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-content-report-plugin:resources";
    private static final String TEMPLATE_NAME = "Confluence.Templates.Plugins.ContentReport.contentReportTable.soy";
    private static final int MAX_RESULTS = 20;
    private static final String DO_SEARCH_URL = "/dosearchsite.action?queryString=";
    private static final String PARAM_SHOW_COMMENTS_COUNT = "showCommentsCount";
    private static final String PARAM_SHOW_LIKES_COUNT = "showLikesCount";
    private final TemplateRenderer templateRenderer;
    private final SearchManager searchManager;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final FormatSettingsManager formatSettingsManager;
    private final ContextPathHolder contextPathHolder;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final LikeManager likeManager;
    private final CommentManager commentManager;
    private final SettingsManager settingsManager;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;
    private final ContentPermissionsQueryFactory contentPermissionsQueryFactory;

    public ContentReportTableMacro(TemplateRenderer templateRenderer, SearchManager searchManager, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, FormatSettingsManager formatSettingsManager, ContextPathHolder contextPathHolder, PermissionManager permissionManager, SpaceManager spaceManager, LikeManager likeManager, CommentManager commentManager, SettingsManager settingsManager, SpacePermissionQueryFactory spacePermissionQueryFactory, ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.templateRenderer = templateRenderer;
        this.searchManager = searchManager;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.contextPathHolder = contextPathHolder;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.likeManager = likeManager;
        this.commentManager = commentManager;
        this.settingsManager = settingsManager;
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
        this.contentPermissionsQueryFactory = contentPermissionsQueryFactory;
    }

    public String execute(Map<String, String> macroParameters, String ignoredBody, ConversionContext conversionContext) throws MacroExecutionException {
        User user = this.getAuthenticatedUser();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(user));
        return this.execute(macroParameters, conversionContext, user, i18NBean, this.createFriendlyDateFormatter(user));
    }

    String execute(Map<String, String> macroParameters, ConversionContext conversionContext, User user, I18NBean i18NBean, FriendlyDateFormatter friendlyDateFormatter) throws MacroExecutionException {
        SearchQuery query = this.createQuery(macroParameters);
        int maxResults = macroParameters.containsKey("maxResults") ? Integer.parseInt(macroParameters.get("maxResults")) : 20;
        List<Searchable> searchables = this.performSearch(query, maxResults);
        ContentReportData contentReportData = this.createReportData(searchables, macroParameters);
        List<Map<Object, Object>> results = this.buildSearchResults(i18NBean, friendlyDateFormatter, searchables, contentReportData);
        return this.renderTemplate(macroParameters, conversionContext, user, contentReportData, results, maxResults);
    }

    private String renderTemplate(Map<String, String> macroParameters, ConversionContext conversionContext, User user, ContentReportData contentReportData, List<?> results, int maxResults) {
        LinkedHashMap templateRenderContext = Maps.newLinkedHashMap();
        templateRenderContext.put("results", results);
        templateRenderContext.put("canViewProfiles", this.permissionManager.hasPermission(user, Permission.VIEW, (Object)new DefaultUser()));
        templateRenderContext.put("contextPath", this.contextPathHolder.getContextPath());
        templateRenderContext.put("analyticsKey", macroParameters.get("analytics-key"));
        templateRenderContext.put(PARAM_SHOW_COMMENTS_COUNT, contentReportData.hasCommentCounts());
        templateRenderContext.put(PARAM_SHOW_LIKES_COUNT, contentReportData.hasLikeCounts());
        boolean showMoreResults = results.size() == maxResults;
        templateRenderContext.put("showMoreResults", showMoreResults);
        if (showMoreResults) {
            String queryString = this.buildSearchMoreResultsLinkUrl(macroParameters);
            templateRenderContext.put("searchMoreResultsLinkUrl", queryString);
        }
        String blueprintModuleCompleteKey = macroParameters.get("blueprintModuleCompleteKey");
        if (results.isEmpty() && StringUtils.isNotBlank((CharSequence)blueprintModuleCompleteKey)) {
            templateRenderContext.put("blankTitle", macroParameters.get("blankTitle"));
            templateRenderContext.put("blankDescription", macroParameters.get("blankDescription"));
            templateRenderContext.put("blueprintKey", this.getModuleKey(blueprintModuleCompleteKey));
            String contentBlueprintId = macroParameters.get("contentBlueprintId");
            templateRenderContext.put("contentBlueprintId", contentBlueprintId);
            String spaceKey = conversionContext.getSpaceKey();
            templateRenderContext.put("dataSpaceKey", spaceKey);
            Space space = this.spaceManager.getSpace(spaceKey);
            boolean canCreate = this.permissionManager.hasCreatePermission(this.getAuthenticatedUser(), (Object)space, Page.class);
            templateRenderContext.put("createButtonLabel", canCreate ? macroParameters.get("createButtonLabel") : null);
            String createContentUrl = this.getCreateContentUrl(contentBlueprintId, spaceKey);
            templateRenderContext.put("createContentUrl", createContentUrl);
        }
        StringBuilder templateBuffer = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)templateBuffer, TEMPLATE_PROVIDER_PLUGIN_KEY, TEMPLATE_NAME, (Map)templateRenderContext);
        return templateBuffer.toString();
    }

    private User getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private String buildSearchMoreResultsLinkUrl(Map<String, String> macroParameters) {
        Set<String> spaceKeys;
        Object queryString = DO_SEARCH_URL;
        Set<String> labels = this.getLabels(macroParameters);
        if (!labels.isEmpty()) {
            queryString = (String)queryString + "labelText:(" + StringUtils.join(labels, (String)"+OR+") + ")";
        }
        if (!(spaceKeys = this.getSpaceKeys(macroParameters)).isEmpty()) {
            queryString = (String)queryString + "+AND+spacekey:(" + StringUtils.join(spaceKeys, (String)"+OR+") + ")";
        }
        queryString = (String)queryString + "&type=page,blog";
        return queryString;
    }

    private List<Map<Object, Object>> buildSearchResults(I18NBean i18NBean, FriendlyDateFormatter friendlyDateFormatter, List<Searchable> searchables, ContentReportData contentReportData) {
        LinkedList results = Lists.newLinkedList();
        for (Searchable searchable : searchables) {
            if (!(searchable instanceof AbstractPage)) continue;
            AbstractPage abstractPage = (AbstractPage)searchable;
            ImmutableMap<Object, Object> result = this.createSearchResult(i18NBean, friendlyDateFormatter, contentReportData, abstractPage);
            results.add(result);
        }
        return results;
    }

    private ImmutableMap<Object, Object> createSearchResult(I18NBean i18NBean, FriendlyDateFormatter friendlyDateFormatter, ContentReportData contentReportData, AbstractPage abstractPage) {
        ConfluenceUser creator = abstractPage.getCreator();
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)"title", (Object)abstractPage.getTitle()).put((Object)"urlPath", (Object)(this.contextPathHolder.getContextPath() + abstractPage.getUrlPath())).put((Object)"creatorName", (Object)(creator == null ? i18NBean.getText("anonymous.name") : creator.getName())).put((Object)"creatorFullName", (Object)(creator == null ? i18NBean.getText("anonymous.name") : creator.getFullName())).put((Object)"friendlyModificationDate", (Object)i18NBean.getText(friendlyDateFormatter.getFormatMessage(abstractPage.getLastModificationDate()))).put((Object)"sortableDate", (Object)Long.toString(abstractPage.getLastModificationDate().getTime()));
        if (contentReportData.hasCommentCounts()) {
            builder.put((Object)"commentCount", (Object)contentReportData.getCommentCount((Searchable)abstractPage));
        }
        if (contentReportData.hasLikeCounts()) {
            builder.put((Object)"likeCount", (Object)contentReportData.getLikeCount((Searchable)abstractPage));
        }
        return builder.build();
    }

    private List<Searchable> performSearch(SearchQuery query, int maxResults) throws MacroExecutionException {
        ContentSearch search = new ContentSearch(query, (SearchSort)ModifiedSort.DESCENDING, 0, maxResults);
        try {
            return this.searchManager.searchEntities((ISearch)search, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        }
        catch (InvalidSearchException e) {
            throw new MacroExecutionException("Invalid search", (Throwable)e);
        }
    }

    private SearchQuery createQuery(Map<String, String> macroParameters) {
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        Set<String> labels = this.getLabels(macroParameters);
        queryBuilder.addMust((Object)this.getLabelQuery(labels));
        queryBuilder.addMust((Object)new ContentTypeQuery((Collection)ImmutableSet.of((Object)ContentTypeEnum.PAGE, (Object)ContentTypeEnum.BLOG)));
        Set<String> spaceKeys = this.getSpaceKeys(macroParameters);
        if (!spaceKeys.isEmpty()) {
            queryBuilder.addMust((Object)new InSpaceQuery(spaceKeys));
        }
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        this.contentPermissionsQueryFactory.create(remoteUser).ifPresent(arg_0 -> ((BooleanQuery.Builder)queryBuilder).addFilter(arg_0));
        queryBuilder.addFilter(this.spacePermissionQueryFactory.create(remoteUser));
        return queryBuilder.build();
    }

    private Set<String> getLabels(Map<String, String> macroParameters) {
        String labelsParameter = macroParameters.get("labels");
        return ContentReportTableMacro.splitTrimToSet(labelsParameter, ",");
    }

    private Set<String> getSpaceKeys(Map<String, String> macroParameters) {
        String spacesParameter = macroParameters.get("spaces");
        return ContentReportTableMacro.splitTrimToSet(spacesParameter, ",");
    }

    private BooleanQuery getLabelQuery(Set<String> labels) {
        BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
        for (String label : labels) {
            booleanQueryFactory.addShould((SearchQuery)new LabelQuery(label));
        }
        return booleanQueryFactory.toBooleanQuery();
    }

    static Set<String> splitTrimToSet(String str, String delimiter) {
        if (StringUtils.isBlank((CharSequence)str)) {
            return Collections.emptySet();
        }
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (String token : str.split(delimiter)) {
            String trimmed = StringUtils.trim((String)token);
            if (!StringUtils.isNotBlank((CharSequence)trimmed)) continue;
            builder.add((Object)trimmed);
        }
        return builder.build();
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private FriendlyDateFormatter createFriendlyDateFormatter(User user) {
        ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences(user);
        DateFormatter dateFormatter = new DateFormatter(pref.getTimeZone(), this.formatSettingsManager, this.localeManager);
        return new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
    }

    private ContentReportData createReportData(List<Searchable> searchables, Map<String, String> macroParams) {
        Map commentCountsMap = null;
        if (this.isParamEnabled(macroParams, PARAM_SHOW_COMMENTS_COUNT)) {
            commentCountsMap = this.commentManager.countComments(searchables);
        }
        Map likeCountsMap = null;
        if (this.isParamEnabled(macroParams, PARAM_SHOW_LIKES_COUNT)) {
            likeCountsMap = this.likeManager.countLikes(searchables);
        }
        return new ContentReportData(commentCountsMap, likeCountsMap);
    }

    private boolean isParamEnabled(Map<String, String> macroParams, String paramName) {
        return Boolean.parseBoolean(macroParams.get(paramName));
    }

    private String getCreateContentUrl(String contentBlueprintId, String spaceKey) {
        Object baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        baseUrl = (String)baseUrl + "/plugins/createcontent/createpage.action";
        UrlBuilder createContentUrl = new UrlBuilder((String)baseUrl);
        createContentUrl.add("spaceKey", spaceKey);
        createContentUrl.add("blueprintModuleCompleteKey", contentBlueprintId);
        return createContentUrl.toString();
    }

    private String getModuleKey(String blueprintModuleCompleteKey) {
        return new ModuleCompleteKey(blueprintModuleCompleteKey).getModuleKey();
    }

    private static class ContentReportData {
        private final Map<Searchable, Integer> commentCountsMap;
        private final Map<Searchable, Integer> likeCountsMap;

        public ContentReportData(Map<Searchable, Integer> commentCountsMap, Map<Searchable, Integer> likeCountsMap) {
            this.commentCountsMap = commentCountsMap;
            this.likeCountsMap = likeCountsMap;
        }

        public Integer getLikeCount(Searchable content) {
            Integer likes = this.likeCountsMap.get(content);
            return likes != null ? likes : 0;
        }

        public Integer getCommentCount(Searchable content) {
            Integer comments = this.commentCountsMap.get(content);
            return comments != null ? comments : 0;
        }

        public boolean hasLikeCounts() {
            return this.likeCountsMap != null;
        }

        public boolean hasCommentCounts() {
            return this.commentCountsMap != null;
        }
    }
}

