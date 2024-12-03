/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.themes.Theme
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.confluence.themes.ThemeResource
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.user.User
 *  com.opensymphony.util.TextUtils
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.gadgets.confluencepagegadget;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugins.gadgets.confluencepagegadget.PageGadgetBean;
import com.atlassian.confluence.plugins.gadgets.error.ErrorCollection;
import com.atlassian.confluence.plugins.gadgets.error.ValidationError;
import com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.user.User;
import com.opensymphony.util.TextUtils;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/pagegadget")
@AnonymousAllowed
@Produces(value={"application/xml", "application/json"})
@Deprecated
public class PageGadgetResource {
    private static final int URL_SCHEMA_LENGTH = 8;
    private PageManager pageManager;
    private Renderer viewRenderer;
    private SettingsManager settingsManager;
    private ConfluenceWebResourceManager webResourceManager;
    private ThemeManager themeManager;
    private PermissionManager permissionManager;
    private UserAccessor userAccessor;

    public PageGadgetResource(PageManager pageManager, @Qualifier(value="viewRenderer") Renderer viewRenderer, SettingsManager settingsManager, ConfluenceWebResourceManager webResourceManager, ThemeManager themeManager, PermissionManager permissionManager, UserAccessor userAccessor) {
        this.pageManager = pageManager;
        this.viewRenderer = viewRenderer;
        this.settingsManager = settingsManager;
        this.webResourceManager = webResourceManager;
        this.themeManager = themeManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
    }

    @GET
    @Path(value="/getrenderedpage")
    public Response getRendererHtml(@Context AuthenticationContext authenticationContext, @QueryParam(value="pageId") String pageId) {
        ConfluenceUser user = this.getUserFromPrincipal(authenticationContext.getPrincipal());
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        AbstractPage page = null;
        if (pageId.length() > 0) {
            page = this.pageManager.getAbstractPage(Long.parseLong(pageId));
        }
        if (page == null || page.isDeleted()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(this.cacheControlNoCache()).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(this.cacheControlNoCache()).build();
        }
        String spaceKey = page.getSpaceKey();
        this.webResourceManager.requireResourcesForContext("main");
        this.webResourceManager.requireResourcesForContext("page");
        this.webResourceManager.requireResourcesForContext("viewcontent");
        this.webResourceManager.requireResourcesForContext("pagegadget");
        PageContext context = page.toPageContext();
        context.setOutputType(ConfluenceRenderContextOutputType.PAGE_GADGET.toString());
        String htmlContent = this.viewRenderer.render((ContentEntityObject)page, (ConversionContext)new DefaultConversionContext((RenderContext)context));
        StringBuffer resources = new StringBuffer();
        resources.append(this.webResourceManager.getCssResources());
        resources.append("<link rel=\"stylesheet\" href=\"" + this.webResourceManager.getSpaceCssPrefix(spaceKey) + "/styles/colors.css?spaceKey=" + GeneralUtil.htmlEncode((String)spaceKey) + "\" type=\"text/css\">\n");
        resources.append(this.getThemeResources(spaceKey));
        resources.append("<link rel=\"stylesheet\" href=\"" + this.webResourceManager.getSpaceCssPrefix(spaceKey) + "/styles/custom.css?spaceKey=" + GeneralUtil.htmlEncode((String)spaceKey) + "\" type=\"text/css\">\n");
        resources.append(this.webResourceManager.getJsResources());
        resources.append(this.webResourceManager.getThemeJsResources(spaceKey));
        htmlContent = UrlUtil.correctBaseUrls((String)htmlContent, (String)baseUrl);
        String resourcesString = UrlUtil.correctBaseUrls((String)resources.toString(), (String)baseUrl);
        String template = "templates/pagegadget/page-gadget.vm";
        Map params = MacroUtils.defaultVelocityContext();
        params.put("resourcesHtml", resourcesString);
        params.put("bodyHtml", htmlContent);
        params.put("baseUrl", baseUrl);
        params.put("serverUrl", PageGadgetResource.getServerUrl(baseUrl));
        params.put("pageUrl", page.getUrlPath());
        params.put("title", page.getTitle());
        String iframe = VelocityUtils.getRenderedTemplate((String)template, (Map)params);
        boolean userCanEditPage = this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)page);
        String contentType = page.getType();
        PageGadgetBean pageGadgetBean = new PageGadgetBean(htmlContent, resourcesString, iframe, userCanEditPage, contentType, page.getTitle());
        return Response.ok((Object)pageGadgetBean).cacheControl(this.cacheControlNoCache()).build();
    }

    @GET
    @Path(value="/validate")
    public Response validate(@Context AuthenticationContext authenticationContext, @QueryParam(value="pageId") String pageId) {
        ConfluenceUser user = this.getUserFromPrincipal(authenticationContext.getPrincipal());
        AbstractPage page = this.getPageWithPermissionCheck((User)user, pageId);
        ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
        if (pageId.length() == 0) {
            errors.add(new ValidationError("pageId", "You must select a page"));
        } else if (page == null) {
            errors.add(new ValidationError("pageId", "Page not found"));
        } else {
            return Response.status((Response.Status)Response.Status.OK).cacheControl(this.cacheControlNoCache()).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorCollection(new ArrayList<String>(), errors)).cacheControl(this.cacheControlNoCache()).build();
    }

    private ConfluenceUser getUserFromPrincipal(Principal principal) {
        ConfluenceUser user = principal != null ? this.userAccessor.getUserByName(principal.getName()) : null;
        return user;
    }

    private AbstractPage getPageWithPermissionCheck(User user, String id) {
        AbstractPage page = null;
        if (id.length() > 0) {
            page = this.pageManager.getAbstractPage(Long.parseLong(id));
        }
        if (page == null || page.isDeleted() || !this.permissionManager.hasPermission(user, Permission.VIEW, (Object)page)) {
            return null;
        }
        return page;
    }

    private CacheControl cacheControlNoCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        return cacheControl;
    }

    private static String getServerUrl(String baseUrl) {
        String result = baseUrl;
        int firstSlash = result.indexOf(47, 8);
        if (firstSlash >= 0) {
            result = result.substring(0, firstSlash);
        }
        return result;
    }

    private String getThemeResources(String spaceKey) {
        Theme theme = this.themeManager.getSpaceTheme(spaceKey);
        StringBuffer themeResources = new StringBuffer();
        boolean isIe = this.isUserAgentIE();
        ArrayList<ThemeResource> stylesheets = new ArrayList<ThemeResource>(theme.getStylesheets().size());
        for (ThemeResource stylesheet : theme.getStylesheets()) {
            if (isIe) {
                stylesheets.add(stylesheet);
                continue;
            }
            if (stylesheet.isIeOnly()) continue;
            stylesheets.add(stylesheet);
        }
        for (ThemeResource stylesheet : stylesheets) {
            if (stylesheet.getLocation().endsWith(".vm")) {
                themeResources.append("<link rel=\"stylesheet\" href=\"" + GeneralUtil.htmlEncode((String)(this.webResourceManager.getSpaceCssPrefix(spaceKey) + "/styles/theme-colors.css?completeModuleKey=" + stylesheet.getCompleteModuleKey() + "&stylesheetName=" + stylesheet.getName() + "&spaceKey=" + GeneralUtil.htmlEncode((String)spaceKey))) + "\" type=\"text/css\">\n");
                continue;
            }
            themeResources.append("<link rel=\"stylesheet\" href=\"" + this.webResourceManager.getStaticPluginResource(stylesheet.getCompleteModuleKey(), stylesheet.getName()) + "\" type=\"text/css\">\n");
        }
        return themeResources.toString();
    }

    private boolean isUserAgentIE() {
        if (ServletContextThreadLocal.getRequest() == null) {
            return false;
        }
        String userAgent = ServletContextThreadLocal.getRequest().getHeader("User-Agent");
        return TextUtils.stringSet((String)userAgent) && userAgent.indexOf("MSIE") != -1;
    }
}

