/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.links.linktypes.PageCreateLink
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkRenderer
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.util.UrlUtil
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractPageExportContentResource;
import com.atlassian.confluence.links.linktypes.PageCreateLink;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.util.UrlUtil;
import com.opensymphony.util.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageWordExportContentResourceImpl
extends AbstractPageExportContentResource {
    public static final String DISPLAY_NAME_SUFFIX = ".doc";
    private static final String CONTENT_TYPE = "application/vnd.ms-word";
    private final ContextPathHolder contextPathHolder;
    private final SettingsManager settingsManager;
    private final XhtmlContent xhtmlContent;

    public PageWordExportContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport ContextPathHolder contextPathHolder, @ComponentImport SettingsManager settingsManager, @ComponentImport PageManager pageManager, @ComponentImport XhtmlContent xhtmlContent, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, pageManager, spaceKey, pageTitle);
        this.contextPathHolder = contextPathHolder;
        this.settingsManager = settingsManager;
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentWordExportHidden((ContentEntityObject)this.getPage());
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        outputContext.setProperty("Cache-Control", "max-age=5");
        outputContext.setProperty("Pragma", "");
        outputContext.setProperty("Expires", new StringBuffer().append(System.currentTimeMillis() + 300L).toString());
        super.spool(outputContext);
    }

    @Override
    protected String getExportSuffix() {
        return DISPLAY_NAME_SUFFIX;
    }

    @Override
    protected InputStream getContentInternal() throws IOException {
        Page page = this.getPage();
        PageContext context = page.toPageContext();
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        context.setBaseUrl(globalSettings.getBaseUrl());
        context.setSiteRoot(this.contextPathHolder.getContextPath());
        context.setOutputType("word");
        context.setLinkRenderer((LinkRenderer)new WordDocLinkRenderer(context));
        String renderedContent = "";
        try {
            renderedContent = this.xhtmlContent.convertStorageToView(page.getBodyContent().getBody(), (ConversionContext)new DefaultConversionContext((RenderContext)context));
        }
        catch (XMLStreamException e) {
            throw new IOException(e);
        }
        catch (XhtmlException e) {
            throw new IOException(e);
        }
        HashMap<String, String> contextMap = new HashMap<String, String>();
        contextMap.put("renderedContent", renderedContent);
        contextMap.put("domainName", globalSettings.getBaseUrl());
        contextMap.put("page", (String)page);
        return new ByteArrayInputStream(VelocityUtils.getRenderedTemplate((String)"templates/extra/webdav/exportword.vm", contextMap).getBytes(globalSettings.getDefaultEncoding()));
    }

    @Override
    protected String getContentType() {
        return new StringBuffer(CONTENT_TYPE).toString();
    }

    @Override
    public String getDisplayName() {
        return this.getPage().getTitle() + DISPLAY_NAME_SUFFIX;
    }

    private static final class WordDocLinkRenderer
    implements LinkRenderer {
        private final PageContext context;

        public WordDocLinkRenderer(PageContext context) {
            this.context = context;
        }

        public String renderLink(Link link, RenderContext renderContext) {
            StringBuffer buffer = new StringBuffer();
            if (link instanceof UnresolvedLink || link instanceof UnpermittedLink || link instanceof PageCreateLink) {
                buffer.append(link.getLinkBody());
                return buffer.toString();
            }
            buffer.append("<a href=\"");
            if (link.isRelativeUrl()) {
                buffer.append(this.context.getBaseUrl());
            }
            buffer.append(link.getUrl());
            buffer.append("\"");
            if (TextUtils.stringSet((String)link.getTitle())) {
                buffer.append(" title=\"").append(link.getTitle()).append("\"");
            }
            buffer.append(">");
            buffer.append(UrlUtil.escapeUrlFirstCharacter((String)UrlUtil.escapeSpecialCharacters((String)link.getLinkBody())));
            buffer.append("</a>");
            return buffer.toString();
        }
    }
}

