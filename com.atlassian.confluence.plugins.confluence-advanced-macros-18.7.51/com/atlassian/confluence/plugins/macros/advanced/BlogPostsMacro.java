/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.ContentFilteringMacro
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.params.ParameterException
 *  com.atlassian.confluence.macro.query.BooleanQueryFactory
 *  com.atlassian.confluence.macro.query.InclusionCriteria
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.ContentIncludeStack
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.CreatorQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.ExcerptHelper
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.InvalidDurationException
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.ContentFilteringMacro;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.InclusionCriteria;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.InvalidDurationException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.opensymphony.util.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogPostsMacro
extends ContentFilteringMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(BlogPostsMacro.class);
    private static final String TEMPLATE_BLOG_POST_TITLES = "com/atlassian/confluence/plugins/macros/advanced/blog-posts-titles.vm";
    private static final String TEMPLATE_BLOG_POST_TITLES_MOBILE = "com/atlassian/confluence/plugins/macros/advanced/blog-posts-titles-mobile.vm";
    private static final String TEMPLATE_BLOG_POST = "com/atlassian/confluence/plugins/macros/advanced/blog-posts.vm";
    private static final String MATCH_LABELS = "match-labels";
    private static final String MATCH_LABELS_ANY = "any";
    private static final String MATCH_LABELS_ALL = "all";
    private static final String CONTENT = "content";
    private static final String CONTENT_EXCERPTS = "excerpts";
    private static final String CONTENT_TITLES = "titles";
    private static final String CONTENT_TITLES_ALIAS = "title";
    private static final String TIME = "time";
    private static final int EXCERPT_LENGTH = 500;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private ExcerptHelper excerptHelper;
    private Renderer viewRenderer;
    private VelocityHelperService velocityHelperService;
    private FormatSettingsManager formatSettingsManager;
    private UserAccessor userAccessor;
    private PermissionManager permissionManager;
    private SpacePermissionQueryFactory spacePermissionQueryFactory;
    private ContentPermissionsQueryFactory contentPermissionsQueryFactory;

    public BlogPostsMacro(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, ExcerptHelper excerptHelper, Renderer viewRenderer, VelocityHelperService velocityHelperService, UserAccessor userAccessor, PermissionManager permissionManager, SpacePermissionQueryFactory spacePermissionQueryFactory, ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.setI18NBeanFactory(i18NBeanFactory);
        this.setLocaleManager(localeManager);
        this.setExcerptHelper(excerptHelper);
        this.setViewRenderer(viewRenderer);
        this.setVelocityHelperService(velocityHelperService);
        this.setFormatSettingsManager(this.formatSettingsManager);
        this.setUserAccessor(userAccessor);
        this.setPermissionManager(permissionManager);
        this.setSpacePermissionQueryFactory(spacePermissionQueryFactory);
        this.setContentPermissionsQueryFactory(contentPermissionsQueryFactory);
        this.spaceKeyParam.setDefaultValue("@self");
        this.maxResultsParam.addParameterAlias("0");
        this.maxResultsParam.setDefaultValue("15");
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    public BlogPostsMacro() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setExcerptHelper(ExcerptHelper excerptHelper) {
        this.excerptHelper = excerptHelper;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            return super.execute(parameters, body, (RenderContext)conversionContext.getPageContext());
        }
        catch (MacroException e) {
            throw new MacroExecutionException(e.getMessage(), (Throwable)e);
        }
    }

    protected String execute(MacroExecutionContext ctx) throws MacroException {
        PageContext pageContext = ctx.getPageContext();
        Map parameters = ctx.getParams();
        boolean popRequired = false;
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.addMust((Object)new ContentTypeQuery(ContentTypeEnum.BLOG));
        try {
            Set authors;
            BooleanQueryFactory spaceKeyQuery;
            String time;
            String labelFilterMode;
            ContentEntityObject ceo = pageContext.getEntity();
            if (ceo instanceof BlogPost) {
                BlogPost blogPost = (BlogPost)ceo;
                if (ContentIncludeStack.contains((ContentEntityObject)blogPost)) {
                    throw new MacroException(this.getText("blogposts.error.already-included-page", StringEscapeUtils.escapeHtml4((String)blogPost.getDisplayTitle())));
                }
                ContentIncludeStack.push((ContentEntityObject)blogPost);
                popRequired = true;
            }
            if ((labelFilterMode = parameters.getOrDefault(MATCH_LABELS, "")).equalsIgnoreCase(MATCH_LABELS_ALL)) {
                this.labelParam.setDefaultInclusionCriteria(InclusionCriteria.ALL);
            } else {
                this.labelParam.setDefaultInclusionCriteria(InclusionCriteria.ANY);
                if (labelFilterMode.length() > 0 && !labelFilterMode.equalsIgnoreCase(MATCH_LABELS_ANY)) {
                    throw new MacroException(this.getText("blogposts.error.invalid-label-filter", MATCH_LABELS_ALL, MATCH_LABELS_ANY));
                }
            }
            BooleanQueryFactory labelQuery = (BooleanQueryFactory)this.labelParam.findValue(ctx);
            if (labelQuery != null) {
                queryBuilder.addMust((Object)labelQuery.toBooleanQuery());
            }
            if (StringUtils.isNotBlank((CharSequence)(time = (String)parameters.get(TIME)))) {
                try {
                    long duration = DateUtils.getDuration((String)time);
                    Date now = new Date();
                    Date then = new Date(now.getTime() - duration * 1000L);
                    queryBuilder.addMust((Object)new DateRangeQuery(then, now, true, true, DateRangeQuery.DateRangeQueryType.MODIFIED));
                }
                catch (InvalidDurationException e) {
                    throw new MacroException(this.getText("blogposts.error.invalid-time-format", StringEscapeUtils.escapeHtml4((String)time)));
                }
            }
            if ((spaceKeyQuery = (BooleanQueryFactory)this.spaceKeyParam.findValue(ctx)) != null) {
                queryBuilder.addMust((Object)spaceKeyQuery.toBooleanQuery());
            }
            if (!(authors = (Set)this.authorParam.findValue(ctx)).isEmpty()) {
                BooleanQueryFactory authorQueryFactory = new BooleanQueryFactory();
                for (String author : authors) {
                    authorQueryFactory.addShould((SearchQuery)new CreatorQuery(author));
                }
                queryBuilder.addMust((Object)authorQueryFactory.toBooleanQuery());
            }
            ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
            queryBuilder.addFilter(this.spacePermissionQueryFactory.create(remoteUser));
            this.contentPermissionsQueryFactory.create(remoteUser).ifPresent(arg_0 -> ((BooleanQuery.Builder)queryBuilder).addFilter(arg_0));
            ContentSearch search = new ContentSearch(queryBuilder.build(), this.getSearchSort(ctx), 0, this.getMaxResults(ctx));
            SearchResults searchResults = this.searchManager.search((ISearch)search);
            List<BlogPost> blogPosts = this.findBlogPosts(searchResults);
            Map velocityContextMap = this.velocityHelperService.createDefaultVelocityContext();
            String blogPostContentDisplayOption = (String)parameters.get(CONTENT);
            velocityContextMap.put("contentType", blogPostContentDisplayOption);
            velocityContextMap.put("posts", this.toPostHtmlTuple(blogPosts, blogPostContentDisplayOption, ctx));
            Space currentSpace = this.getCurrentSpace(ceo);
            if (currentSpace != null) {
                boolean canCreateBlog = this.permissionManager.hasCreatePermission(this.getUser(), (Object)currentSpace, BlogPost.class);
                velocityContextMap.put("canCreateBlog", canCreateBlog);
            }
            if (StringUtils.equals((CharSequence)"mobile", (CharSequence)pageContext.getOutputDeviceType())) {
                String string = this.velocityHelperService.getRenderedTemplate(TEMPLATE_BLOG_POST_TITLES_MOBILE, velocityContextMap);
                return string;
            }
            if (CONTENT_EXCERPTS.equals(blogPostContentDisplayOption)) {
                String string = this.velocityHelperService.getRenderedTemplate(TEMPLATE_BLOG_POST, velocityContextMap);
                return string;
            }
            if (CONTENT_TITLES.equals(blogPostContentDisplayOption) || CONTENT_TITLES_ALIAS.equals(blogPostContentDisplayOption)) {
                String string = this.velocityHelperService.getRenderedTemplate(TEMPLATE_BLOG_POST_TITLES, velocityContextMap);
                return string;
            }
            String string = this.velocityHelperService.getRenderedTemplate(TEMPLATE_BLOG_POST, velocityContextMap);
            return string;
        }
        catch (Exception e) {
            log.error(e.getMessage(), (Throwable)e);
            throw new MacroException(e.getMessage(), (Throwable)e);
        }
        finally {
            if (popRequired) {
                ContentIncludeStack.pop();
            }
        }
    }

    private SearchSort getSearchSort(MacroExecutionContext ctx) throws MacroException {
        try {
            SearchSort paramSearchSort = (SearchSort)this.sortParam.findValue(ctx);
            if (paramSearchSort != null) {
                return paramSearchSort;
            }
        }
        catch (ParameterException pe) {
            throw new MacroException(this.getText("blogposts.error.parse-reverse-or-sort-param", new Object[0]), (Throwable)pe);
        }
        return new CreatedSort(SearchSort.Order.DESCENDING);
    }

    private int getMaxResults(MacroExecutionContext ctx) throws MacroException {
        try {
            return (Integer)this.maxResultsParam.findValue(ctx);
        }
        catch (ParameterException pe) {
            throw new MacroException(this.getText("blogposts.error.invalid-max-posts", StringEscapeUtils.escapeHtml4((String)"max")));
        }
    }

    List<BlogPost> findBlogPosts(SearchResults searchResults) {
        return Lists.newArrayList((Iterable)Collections2.transform((Collection)this.searchManager.convertToEntities(searchResults, SearchManager.EntityVersionPolicy.LATEST_VERSION), searchable -> (BlogPost)searchable));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<PostHtmlTuple> toPostHtmlTuple(List<BlogPost> blogPosts, String contentType, MacroExecutionContext ctx) {
        ArrayList<PostHtmlTuple> list = new ArrayList<PostHtmlTuple>(blogPosts.size());
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        for (BlogPost post : blogPosts) {
            ContentIncludeStack.push((ContentEntityObject)post);
            try {
                String renderedHtml;
                if (CONTENT_TITLES.equals(contentType) || CONTENT_TITLES_ALIAS.equals(contentType)) {
                    renderedHtml = "";
                } else {
                    String excerpt = this.getContent(post, contentType);
                    PageContext renderContext = new PageContext((ContentEntityObject)post, ctx.getPageContext());
                    renderedHtml = this.viewRenderer.render(excerpt, (ConversionContext)new DefaultConversionContext((RenderContext)renderContext));
                    Pattern htmlErrorString = Pattern.compile("<div class=\"error\">.*</div>");
                    Matcher match = htmlErrorString.matcher(renderedHtml);
                    renderedHtml = match.find() ? match.group(0) : renderedHtml;
                }
                list.add(new PostHtmlTuple(post, renderedHtml, new DateFormatter(this.userAccessor.getConfluenceUserPreferences((User)currentUser).getTimeZone(), this.formatSettingsManager, this.localeManager)));
            }
            finally {
                ContentIncludeStack.pop();
            }
        }
        return list;
    }

    private String getContent(BlogPost post, String contentType) {
        Object excerpt;
        if (CONTENT_EXCERPTS.equals(contentType)) {
            excerpt = this.excerptHelper.getExcerpt((ContentEntityObject)post);
            if (StringUtils.isBlank((CharSequence)excerpt)) {
                excerpt = this.excerptHelper.getText(post.getBodyAsString());
                excerpt = ((String)excerpt).length() > 500 ? HtmlUtil.htmlEncode((String)TextUtils.trimToEndingChar((String)excerpt, (int)500)) + "&hellip;" : HtmlUtil.htmlEncode((String)excerpt);
                return excerpt;
            }
        } else {
            excerpt = post.getBodyAsString();
        }
        return excerpt;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private String getText(String i18nkey, Object ... args) {
        return this.getI18nBean().getText(i18nkey, args);
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    private User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private Space getCurrentSpace(ContentEntityObject ceo) {
        Space currentSpace = null;
        if (ceo instanceof Spaced) {
            currentSpace = ((Spaced)ceo).getSpace();
        }
        return currentSpace;
    }

    public void setContentPermissionsQueryFactory(ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.contentPermissionsQueryFactory = contentPermissionsQueryFactory;
    }

    public void setSpacePermissionQueryFactory(SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    public static class PostHtmlTuple {
        private BlogPost post;
        private String renderedHtml;
        private FriendlyDateFormatter friendlyDateFormatter;

        public PostHtmlTuple(BlogPost post, String renderedHtml, DateFormatter dateFormatter) {
            this.post = post;
            this.renderedHtml = renderedHtml;
            this.friendlyDateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
        }

        public Message getFormattedDate() {
            return this.friendlyDateFormatter.getFormatMessage(this.post.getCreationDate());
        }

        public BlogPost getPost() {
            return this.post;
        }

        public String getRenderedHtml() {
            return this.renderedHtml;
        }
    }
}

