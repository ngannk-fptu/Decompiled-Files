/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Contained
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.UserI18NBeanFactory
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.seraph.util.RedirectUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.bnpparibas.confluence.extra.tree;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.seraph.util.RedirectUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTree
extends BaseMacro
implements Macro {
    private static final Logger logger = LoggerFactory.getLogger(PageTree.class);
    private static final String PLACEMENT = "placement";
    private static final String PARAM_ROOT = "root";
    private static final String ROOT_SELF = "@self";
    private static final String ROOT_PARENT = "@parent";
    private static final String ROOT_HOME = "@home";
    private static final String ROOT_NONE = "@none";
    private static final String PARAM_EXCERPT = "excerpt";
    private static final String PARAM_SORT = "sort";
    private static final String PARAM_REVERSE = "reverse";
    private static final String PARAM_SEARCHBOX = "searchBox";
    private static final String PARAM_EXP_COL_ALL = "expandCollapseAll";
    private static final String PARAM_STARTDEPTH = "startDepth";
    private static final String PARAM_SPACEKEY = "spaces";
    private static final String PARAM_EXPAND_CURRENT = "expandCurrent";
    private PageManager pageManager;
    private BootstrapManager bootstrapManager;
    private SpaceManager spaceManager;
    private WebResourceManager webResourceManager;
    private LocaleManager localeManager;
    private I18NBeanFactory i18NBeanFactory;
    private HttpContext httpContext;

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private I18NBean i18n() {
        UserI18NBeanFactory factory = new UserI18NBeanFactory();
        factory.setLocaleManager(this.localeManager);
        factory.setI18NBeanFactory(this.i18NBeanFactory);
        return factory.getI18NBean();
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public String getWebAppContextPath() {
        return this.bootstrapManager.getWebAppContextPath();
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setWebResourceManager(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    protected AbstractPage getAncestorPage(ConversionContext context) {
        PageContext pageContext = context.getPageContext();
        if (pageContext == null) {
            return null;
        }
        ContentEntityObject container = this.getTopLevelContainer(pageContext.getOriginalContext().getEntity());
        return container instanceof AbstractPage ? (AbstractPage)container : null;
    }

    private ContentEntityObject getTopLevelContainer(ContentEntityObject ceo) {
        if (!(ceo instanceof Contained)) {
            return ceo;
        }
        while (ceo instanceof Contained) {
            ceo = ((Contained)ceo).getContainer();
        }
        return ceo;
    }

    public String getName() {
        return "pagetree";
    }

    private boolean disableLinks(ConversionContext conversionContext) {
        return "preview".equals(conversionContext.getOutputType());
    }

    protected String getHttpServletRequestLoginUrl(HttpServletRequest httpServletRequest) {
        return RedirectUtils.getLoginUrl((HttpServletRequest)httpServletRequest);
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) throws Exception {
        return VelocityUtils.getRenderedTemplate((String)"vm/tree.vm", contextMap);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    protected List<Page> generateAncestors(AbstractPage targetPage, Page rootPage) throws MacroExecutionException {
        LinkedList<Page> ancestors = null;
        if (targetPage instanceof Page) {
            Page ancestorPage = (Page)targetPage;
            ancestors = new LinkedList<Page>();
            HashSet<Page> visitedPages = new HashSet<Page>();
            while (ancestorPage != null && ancestorPage.getId() != rootPage.getId()) {
                if (ancestorPage.getId() != rootPage.getId() && visitedPages.contains(ancestorPage)) {
                    logger.error("Cyclic loop detected in ancestors of {}. Page id {} is referenced twice.", (Object)targetPage.getId(), (Object)ancestorPage.getId());
                    throw new MacroExecutionException(this.i18n().getText("pagetree.error.cyclicloop"));
                }
                visitedPages.add(ancestorPage);
                if ((ancestorPage = ancestorPage.getParent()) == null) continue;
                ancestors.add(ancestorPage);
            }
        }
        return ancestors;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        Page rootPage;
        String spaceKey;
        String string = spaceKey = parameters.get(PARAM_SPACEKEY) != null ? parameters.get(PARAM_SPACEKEY) : conversionContext.getSpaceKey();
        if (spaceKey == null) {
            return RenderUtils.blockError((String)this.i18n().getText("pagetree.error.unsupportedcontent", Arrays.asList(this.i18n().getText("com.atlassian.confluence.plugins.pagetree.pagetree.label"))), (String)"");
        }
        boolean mobile = false;
        if ("mobile".equals(conversionContext.getOutputDeviceType())) {
            this.webResourceManager.requireResourcesForContext("atl.confluence.plugins.pagetree-mobile");
            mobile = true;
        } else {
            this.webResourceManager.requireResourcesForContext("atl.confluence.plugins.pagetree-desktop");
        }
        String rootPageName = parameters.get(PARAM_ROOT);
        ArrayList<String> errors = new ArrayList<String>();
        boolean noRoot = false;
        if (rootPageName == null || ROOT_HOME.equalsIgnoreCase(rootPageName)) {
            Space space = this.spaceManager.getSpace(spaceKey);
            rootPage = space.getHomePage();
        } else if (ROOT_SELF.equalsIgnoreCase(rootPageName)) {
            rootPage = (Page)conversionContext.getEntity();
        } else if (ROOT_PARENT.equalsIgnoreCase(rootPageName)) {
            rootPage = ((Page)conversionContext.getEntity()).getParent();
        } else if (ROOT_NONE.equalsIgnoreCase(rootPageName)) {
            rootPage = new Page();
            noRoot = true;
        } else {
            rootPage = this.pageManager.getPage(spaceKey, rootPageName);
        }
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        StringBuffer outputBuffer = new StringBuffer();
        contextMap.put("contextPath", this.getWebAppContextPath());
        if (!noRoot && rootPage == null) {
            errors.add(this.i18n().getText("pagetree.rootpage.notfound", (Object[])new String[]{rootPageName, this.spaceManager.getSpace(spaceKey).getName()}));
            contextMap.put("errors", errors);
        } else {
            AbstractPage targetPage;
            String expandCurrent;
            String startDepth;
            String expandCollapseAll;
            String searchBox;
            String reverse;
            String sort;
            String excerpt = parameters.get(PARAM_EXCERPT);
            if (excerpt == null) {
                excerpt = "false";
            }
            if ((sort = parameters.get(PARAM_SORT)) == null) {
                sort = "position";
            }
            if ((reverse = parameters.get(PARAM_REVERSE)) == null) {
                reverse = "false";
            }
            if ((searchBox = parameters.get(PARAM_SEARCHBOX)) == null) {
                searchBox = "false";
            }
            if ((expandCollapseAll = parameters.get(PARAM_EXP_COL_ALL)) == null) {
                expandCollapseAll = "false";
            }
            if ((startDepth = parameters.get(PARAM_STARTDEPTH)) == null || !StringUtils.isNumeric((CharSequence)startDepth)) {
                startDepth = "0";
            }
            if ((expandCurrent = parameters.get(PARAM_EXPAND_CURRENT)) == null) {
                expandCurrent = "false";
            }
            if ((targetPage = this.getAncestorPage(conversionContext)) == null && conversionContext.hasProperty("currentPage")) {
                try {
                    targetPage = (Page)conversionContext.getProperty("currentPage");
                }
                catch (Exception e) {
                    logger.info("Cannot get current page from conversion context");
                }
            }
            List<Page> ancestors = this.generateAncestors(targetPage, rootPage);
            HttpServletRequest httpServletRequest = this.httpContext.getRequest();
            if (null != httpServletRequest) {
                contextMap.put("loginUrl", this.getHttpServletRequestLoginUrl(httpServletRequest));
            }
            contextMap.put("ancestors", ancestors);
            contextMap.put("rtpage", rootPage);
            contextMap.put("tgtpage", targetPage);
            contextMap.put(PARAM_EXCERPT, excerpt);
            contextMap.put(PARAM_SORT, sort);
            contextMap.put(PARAM_REVERSE, reverse);
            contextMap.put(PARAM_SEARCHBOX, BooleanUtils.toBooleanObject((String)searchBox));
            contextMap.put(PARAM_EXP_COL_ALL, BooleanUtils.toBooleanObject((String)expandCollapseAll));
            contextMap.put(PARAM_STARTDEPTH, startDepth);
            contextMap.put("spaceKey", spaceKey);
            contextMap.put("noRoot", noRoot ? Boolean.TRUE : Boolean.FALSE);
            contextMap.put("disableLinks", this.disableLinks(conversionContext));
            contextMap.put("mobile", mobile);
            contextMap.put(PARAM_EXPAND_CURRENT, expandCurrent);
            String placement = parameters.get(PLACEMENT);
            contextMap.put(PLACEMENT, placement != null ? placement : "");
        }
        try {
            outputBuffer.append(this.getRenderedTemplate(contextMap));
        }
        catch (Exception e) {
            logger.error("Error while trying to display ancestorPage tree", (Throwable)e);
            outputBuffer.append("Error while trying to display ancestorPage tree! ").append("The template used to construct the tree could not be loaded from the filesystem. \n").append("Please contact an administrator with the following stacktrace:  ").append(e);
        }
        return outputBuffer.toString();
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

