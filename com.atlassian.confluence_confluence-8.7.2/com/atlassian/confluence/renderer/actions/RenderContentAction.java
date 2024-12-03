/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.renderer.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.service.IdContentLocator;
import com.atlassian.confluence.content.service.RenderContentCommand;
import com.atlassian.confluence.content.service.RenderingService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

@ReadOnlyAccessAllowed
public class RenderContentAction
extends AbstractCommandAction {
    private RenderingService renderingService;
    private SpaceService spaceService;
    private ThemeManager themeManager;
    private String contentId;
    private String contentType;
    private String spaceKey;
    private String xHtml;
    private String outputType;

    @HtmlSafe
    public String getRenderedContent() {
        return ((RenderContentCommand)this.getBean()).getRenderedContent();
    }

    @Override
    protected ServiceCommand createCommand() {
        IdContentLocator idContentLocator = this.renderingService.getContentLocator(Long.parseLong(this.contentId));
        SpaceLocator spaceLocator = this.spaceService.getKeySpaceLocator(this.spaceKey);
        return this.renderingService.newRenderXHtmlContentCommand(idContentLocator, spaceLocator, this.contentType, this.xHtml, this.getContextOutputType());
    }

    private ConversionContextOutputType getContextOutputType() {
        try {
            return ConversionContextOutputType.valueOf(this.outputType.toUpperCase());
        }
        catch (Exception e) {
            return ConversionContextOutputType.DISPLAY;
        }
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setxHtml(String xHtml) {
        this.xHtml = xHtml;
    }

    public void setRenderingService(RenderingService renderingService) {
        this.renderingService = renderingService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public Theme getActiveTheme() {
        return this.themeManager == null || this.spaceKey == null ? null : this.themeManager.getSpaceTheme(this.spaceKey);
    }
}

