/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.CustomPageSettings
 *  com.atlassian.confluence.core.CustomPageSettingsManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.PageAware
 *  com.atlassian.confluence.pages.actions.PageNotFoundAction
 *  com.atlassian.confluence.pages.actions.ViewBlogPostsByDateAction
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.ia.ui;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.pages.actions.PageNotFoundAction;
import com.atlassian.confluence.pages.actions.ViewBlogPostsByDateAction;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.model.DateNodeBean;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.plugins.ia.service.BlogTreeService;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.plugins.ia.service.SidebarPageService;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.plugins.ia.service.SpaceBeanFactory;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultSoySidebarContextProvider
implements ContextProvider {
    private final BlogTreeService blogTreeService;
    private final PageManager pageManager;
    private final SidebarPageService sidebarPageService;
    private final SidebarLinkService sidebarLinkService;
    private final SpacePermissionManager spacePermissionManager;
    private final SpaceBeanFactory spaceBeanFactory;
    private final XhtmlContent xhtmlContent;
    private final SidebarService sidebarService;
    private final CustomPageSettingsManager customPageSettingsManager;
    private final WikiStyleRenderer wikiStyleRenderer;
    private final FavouriteManager favouriteManager;
    private final AccessModeService accessModeService;
    private final XsrfTokenGenerator simpleXsrfTokenGenerator;
    private static final String COLLECTOR_PAGES = "spacebar-pages";
    private static final String COLLECTOR_BLOGS = "spacebar-blogs";
    private static final String COLLECTOR_SETTINGS = "spacebar-advanced";
    private final Logger log = LoggerFactory.getLogger(DefaultSoySidebarContextProvider.class);

    public DefaultSoySidebarContextProvider(BlogTreeService blogTreeService, SidebarPageService sidebarPageService, SidebarLinkService sidebarLinkService, PageManager pageManager, SpacePermissionManager spacePermissionManager, SpaceBeanFactory spaceBeanFactory, XhtmlContent xhtmlContent, SidebarService sidebarService, CustomPageSettingsManager customPageSettingsManager, WikiStyleRenderer wikiStyleRenderer, FavouriteManager favouriteManager, AccessModeService accessModeService, @ComponentImport XsrfTokenGenerator simpleXsrfTokenGenerator) {
        this.blogTreeService = blogTreeService;
        this.sidebarPageService = sidebarPageService;
        this.sidebarLinkService = sidebarLinkService;
        this.pageManager = pageManager;
        this.spacePermissionManager = spacePermissionManager;
        this.spaceBeanFactory = spaceBeanFactory;
        this.xhtmlContent = xhtmlContent;
        this.sidebarService = sidebarService;
        this.customPageSettingsManager = customPageSettingsManager;
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.favouriteManager = favouriteManager;
        this.accessModeService = accessModeService;
        this.simpleXsrfTokenGenerator = simpleXsrfTokenGenerator;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> params) {
        Object action = params.get("action");
        Object contentId = params.get("contentId");
        AbstractPage page = this.getPage(action, contentId);
        Space space = this.getSpace(action, page, params);
        if (page == null && space == null) {
            this.log.warn("Could not obtain either the Page or Space from the current action [{}] and contentId [{}]]", action, contentId);
            return Collections.emptyMap();
        }
        String collectorKey = this.getCollectorKey((String)params.get("collector-key"), page, action);
        HashMap<String, Object> context = new HashMap<String, Object>();
        String token = this.simpleXsrfTokenGenerator.getToken(ServletActionContext.getRequest(), true);
        context.put("atlToken", token);
        this.populateWithMainSidebarData(context, space, collectorKey, page);
        this.populateWithContextualNavData(context, page, space, collectorKey, action);
        return context;
    }

    @Nullable
    private AbstractPage getPage(Object action, Object contentId) {
        AbstractPage page = null;
        if (action instanceof PageAware) {
            page = ((PageAware)action).getPage();
        } else if (contentId instanceof String) {
            page = this.pageManager.getAbstractPage(Long.parseLong((String)contentId));
        }
        return page != null ? page.getLatestVersion() : null;
    }

    @Nullable
    private Space getSpace(Object action, @Nullable AbstractPage page, Map<String, Object> params) {
        if (page != null) {
            return page.getSpace();
        }
        if (action instanceof Spaced) {
            return ((Spaced)action).getSpace();
        }
        if (params != null && params.containsKey("space") && params.get("space") instanceof Space) {
            return (Space)params.get("space");
        }
        return null;
    }

    @Nonnull
    private String getCollectorKey(@Nullable String collectorKey, @Nullable AbstractPage page, Object action) {
        if (collectorKey == null) {
            if (page == null) {
                return action instanceof PageNotFoundAction ? "" : COLLECTOR_SETTINGS;
            }
            return page instanceof BlogPost ? COLLECTOR_BLOGS : COLLECTOR_PAGES;
        }
        return collectorKey;
    }

    @Nonnull
    private PageContext getPageContext(AbstractPage page, Space space) {
        if (page != null) {
            return new PageContext((ContentEntityObject)page);
        }
        if (space != null) {
            return new PageContext(space.getKey());
        }
        return new PageContext();
    }

    private void populateWithMainSidebarData(Map<String, Object> context, @Nullable Space space, String collectorKey, AbstractPage page) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        List<SidebarLinkBean> quickLinks = this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.QUICK, space.getKey(), false);
        Collections.reverse(quickLinks);
        CustomPageSettings settings = this.customPageSettingsManager.retrieveSettings(space.getKey());
        if (StringUtils.isBlank((CharSequence)settings.getSidebar())) {
            settings = this.customPageSettingsManager.retrieveSettings();
        }
        if (StringUtils.isNotBlank((CharSequence)settings.getSidebar())) {
            PageContext pageContext = this.getPageContext(page, space);
            context.put("hasSidebarCustomisation", true);
            context.put("sidebarCustomisation", this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)pageContext, settings.getSidebar()));
        }
        context.put("space", this.spaceBeanFactory.createSpaceBean(space, (User)user));
        context.put("mainLinks", this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.MAIN, space.getKey(), false));
        context.put("quickLinks", quickLinks);
        context.put("advancedLinks", this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.ADVANCED, space.getKey(), false));
        context.put("forBlogs", COLLECTOR_BLOGS.equals(collectorKey));
        context.put("forSettings", COLLECTOR_SETTINGS.equals(collectorKey));
        context.put("collectorToHighlight", collectorKey);
        context.put("hasCreatePermission", this.spacePermissionManager.hasPermission("EDITSPACE", space, (User)user));
        context.put("hasConfigurePermission", this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, (User)user));
        context.put("quickLinksState", this.sidebarService.getOption(space.getKey(), "quick-links-state"));
        context.put("pageTreeState", this.sidebarService.getOption(space.getKey(), "page-tree-state"));
        context.put("hasFavouriteSpacePermission", this.favouriteManager.hasPermission((User)user, space));
        context.put("isFavouriteSpace", this.favouriteManager.isUserFavourite((User)user, space));
        context.put("accessMode", this.accessModeService.getAccessMode().name());
    }

    private void populateWithContextualNavData(Map<String, Object> context, @Nullable AbstractPage page, @Nullable Space space, String collectorKey, Object action) {
        List<DateNodeBean> blogPosts;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String sidebarNav = this.sidebarService.getOption(space.getKey(), "nav-type");
        sidebarNav = sidebarNav == null ? "page-tree" : sidebarNav;
        context.put("navType", sidebarNav);
        if (COLLECTOR_PAGES.equals(collectorKey) && page != null) {
            if ("page-tree".equals(sidebarNav)) {
                try {
                    Page home = space.getHomePage();
                    String root = this.getRoot((Page)page, home);
                    DefaultConversionContext pageTreePluginConversionContext = new DefaultConversionContext((RenderContext)new PageContext((ContentEntityObject)page));
                    pageTreePluginConversionContext.setProperty("currentPage", (Object)page);
                    context.put("contextualNav", this.xhtmlContent.convertStorageToView("<ac:macro ac:name=\"pagetree\"><ac:parameter ac:name=\"root\">" + root + "</ac:parameter><ac:parameter ac:name=\"expandCurrent\">true</ac:parameter><ac:parameter ac:name=\"placement\">sidebar</ac:parameter></ac:macro>", (ConversionContext)pageTreePluginConversionContext));
                    context.put("pageTreeEmpty", page.equals((Object)home) && !home.hasChildren());
                }
                catch (Exception e) {
                    this.log.debug("Error rendering page tree macro on sidebar", (Throwable)e);
                    context.put("contextualNav", this.sidebarPageService.getPageContextualNav(page.getId()));
                }
            } else {
                context.put("contextualNav", this.sidebarPageService.getPageContextualNav(page.getId()));
            }
        } else if (COLLECTOR_BLOGS.equals(collectorKey) && !(blogPosts = page != null ? this.blogTreeService.getBlogTree((User)user, page.getId()) : (action instanceof ViewBlogPostsByDateAction ? this.blogTreeService.getBlogTree((User)user, space.getKey(), ((ViewBlogPostsByDateAction)action).getPostingDay()) : this.blogTreeService.getBlogTree((User)user, space.getKey(), null))).isEmpty()) {
            context.put("contextualNav", blogPosts);
        }
    }

    private String getRoot(Page currentPage, Page spaceHome) {
        String root = "@home";
        List ancestors = currentPage.getAncestors();
        if (!(currentPage.equals((Object)spaceHome) || ancestors != null && ancestors.contains(spaceHome))) {
            root = "@none";
        }
        return root;
    }
}

