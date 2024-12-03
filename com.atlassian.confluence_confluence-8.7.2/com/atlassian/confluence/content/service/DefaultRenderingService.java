/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.service.IdContentLocator;
import com.atlassian.confluence.content.service.RenderContentCommand;
import com.atlassian.confluence.content.service.RenderContentCommandImpl;
import com.atlassian.confluence.content.service.RenderingService;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;

public class DefaultRenderingService
implements RenderingService {
    private ContentEntityManager contentEntityManager;
    private PermissionManager permissionManager;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private FormatConverter formatConverter;
    private PageTemplateManager pageTemplateManager;

    @Override
    public RenderContentCommand newRenderXHtmlContentCommand(IdContentLocator contentLocator, SpaceLocator spaceLocator, String contentType, String content, ConversionContextOutputType conversionContextOutputType) {
        return new RenderContentCommandImpl(this.permissionManager, contentLocator, spaceLocator, contentType, content, this.formatConverter, conversionContextOutputType, this.pageTemplateManager);
    }

    @Override
    public RenderContentCommand newRenderWikiMarkupContentCommand(IdContentLocator contentLocator, SpaceLocator spaceLocator, String contentType, String content, ConversionContextOutputType conversionContextOutputType) {
        return new RenderContentCommandImpl(this.permissionManager, contentLocator, spaceLocator, contentType, content, this.formatConverter, conversionContextOutputType, this.pageTemplateManager);
    }

    @Override
    public IdContentLocator getContentLocator(long contentId) {
        return new IdContentLocator(this.contentEntityManager, contentId);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public I18NBeanFactory getI18NBeanFactory() {
        return this.i18NBeanFactory;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18nBeanFactory) {
        this.i18NBeanFactory = i18nBeanFactory;
    }

    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setFormatConverter(FormatConverter formatConverter) {
        this.formatConverter = formatConverter;
    }

    public void setPageTemplateManager(PageTemplateManager pageTemplateManager) {
        this.pageTemplateManager = pageTemplateManager;
    }

    public static enum RenderMode {
        RICHTEXT("richtext"),
        MARKUP("markup");

        private final String value;

        private RenderMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}

