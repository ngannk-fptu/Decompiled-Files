/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.links.linktypes.AbstractPageLink
 *  com.atlassian.confluence.links.linktypes.BlogPostLink
 *  com.atlassian.confluence.links.linktypes.PageLink
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.ContentIncludeStack
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.ExcerptHelper
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.links.linktypes.BlogPostLink;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.macros.advanced.IncludeMacroUtils;
import com.atlassian.confluence.plugins.macros.advanced.PageProvider;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.MacroPanel;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcerptIncludeMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(ExcerptIncludeMacro.class);
    private PageProvider pageProvider;
    private PermissionManager permissionManager;
    private ExcerptHelper excerptHelper;
    private Renderer viewRenderer;
    private LinkResolver linkResolver;
    private I18NBeanFactory i18NBeanFactory;

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String linkTitle;
        ContentEntityObject contentEntityObject;
        log.debug("Beginning execute.");
        PageContext context = conversionContext.getPageContext();
        Optional<com.atlassian.confluence.xhtml.api.Link> linkObj = IncludeMacroUtils.getLink(conversionContext);
        if (linkObj.isPresent()) {
            contentEntityObject = this.pageProvider.resolve(linkObj.get(), conversionContext);
            linkTitle = contentEntityObject.getTitle();
        } else {
            String linkText = this.getLinkText(parameters);
            AbstractPageLink link = this.getAbstractPageLink(context, linkText);
            if (link == null) {
                return this.getI18nBean().getText("excerptinclude.error.cannot-link-to", (Object[])new String[]{StringEscapeUtils.escapeHtml4((String)linkText)});
            }
            contentEntityObject = link.getDestinationContent();
            linkTitle = link.getPageTitle();
        }
        String bodyContent = StringUtils.defaultString((String)this.getBodyContent(contentEntityObject, linkTitle, context));
        if (Boolean.parseBoolean(parameters.get("nopanel"))) {
            return bodyContent;
        }
        return MacroPanel.wrap(GeneralUtil.unescapeEntities((String)linkTitle), bodyContent, parameters, (RenderContext)context);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getBodyContent(ContentEntityObject contentEntityObject, String linkTitle, PageContext context) {
        if (contentEntityObject == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)contentEntityObject)) {
            return this.getI18nBean().getText("excerptinclude.error.page-does-not-exists", Collections.singletonList(StringEscapeUtils.escapeHtml4((String)linkTitle)));
        }
        if (ContentIncludeStack.contains((ContentEntityObject)contentEntityObject)) {
            I18NBean i18nBean = this.getI18nBean();
            String message = i18nBean.getText("excerptinclude.error.recursive.message");
            String contents = i18nBean.getText("excerptinclude.error.recursive.contents", Collections.singletonList(StringEscapeUtils.escapeHtml4((String)contentEntityObject.getTitle())));
            return RenderUtils.blockError((String)message, (String)contents);
        }
        try {
            ContentIncludeStack.push((ContentEntityObject)contentEntityObject);
            PageContext excerptContext = new PageContext(contentEntityObject, context);
            String excerpt = this.excerptHelper.getExcerpt(contentEntityObject);
            DefaultConversionContext excerptConversionContext = new DefaultConversionContext((RenderContext)excerptContext);
            String string = this.viewRenderer.render(excerpt, (ConversionContext)excerptConversionContext);
            return string;
        }
        finally {
            ContentIncludeStack.pop();
        }
    }

    private String getLinkText(Map<String, String> parameters) {
        String linkText = parameters.get("0");
        if (StringUtils.isBlank((CharSequence)linkText) && StringUtils.isBlank((CharSequence)(linkText = parameters.get("pageTitle")))) {
            linkText = parameters.get("blogPost");
        }
        return StringUtils.defaultString((String)linkText, (String)"").trim();
    }

    private AbstractPageLink getAbstractPageLink(PageContext context, String linkText) {
        Link link = this.linkResolver.createLink((RenderContext)context, linkText);
        if (!(link instanceof PageLink) && !(link instanceof BlogPostLink)) {
            return null;
        }
        return (AbstractPageLink)link;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setExcerptHelper(ExcerptHelper excerptHelper) {
        this.excerptHelper = excerptHelper;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public void setUserI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean();
    }
}

